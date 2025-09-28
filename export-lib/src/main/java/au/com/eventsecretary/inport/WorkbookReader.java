package au.com.eventsecretary.inport;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.common.Timestamp;
import au.com.eventsecretary.people.Address;
import au.com.eventsecretary.people.AddressImpl;
import au.com.eventsecretary.people.ContactDetails;
import au.com.eventsecretary.people.ContactDetailsImpl;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.people.States;
import au.com.eventsecretary.simm.DateUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static au.com.eventsecretary.CollectionUtility.toIntegerList;
import static au.com.eventsecretary.simm.IdentityUtils.cleanEmailAddress;
import static au.com.eventsecretary.simm.IdentityUtils.cleanName;
import static au.com.eventsecretary.simm.IdentityUtils.cleanPhoneNumber;
import static au.com.eventsecretary.simm.IdentityUtils.splitName;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class WorkbookReader {
    private static final ThreadLocal<Logger> LOGGER_THREAD_LOCAL = new ThreadLocal<>();

    public static void loggerBegin(Logger logger) {
        LOGGER_THREAD_LOCAL.set(logger);
    }
    public static Logger logger() {
        return LOGGER_THREAD_LOCAL.get();
    }
    public static void loggerEnd() {
        LOGGER_THREAD_LOCAL.remove();
    }

    private Workbook workbook;
    private Logger logger;
    private ValidationHandler validationHandler;
    private DataFormatter dataFormatter = new DataFormatter();
    private FormulaEvaluator formulaEvaluator;

    public static interface ValidationHandler {
        void validationError(String sheetName, int row, String column, String message);
    }

    public static WorkbookReader reader() {
        return new WorkbookReader();
    }

    public WorkbookReader logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public WorkbookReader open(File file) {
        try {
            workbook = new XSSFWorkbook(new FileInputStream(file));
            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        } catch (FileNotFoundException e) {
            if (logger != null) {
                logger.error("workbook:notFound:{}", file.getAbsolutePath());
            }
            throw new UnexpectedSystemException(e);
        } catch (IOException e) {
            if (logger != null) {
                logger.error("workbook:error:{}:", file.getAbsolutePath(), e.getMessage());
            }
            throw new UnexpectedSystemException(e);
        }
        return this;
    }

    public WorkbookReader validationHandler(ValidationHandler validationHandler) {
        this.validationHandler = validationHandler;
        return this;
    }

    public WorkbookReader end() {
        try {
            workbook.close();
        } catch (IOException e) {
            logger.warn("workbook:close", e);
        }
        return this;
    }

    public class SheetReader {
        Sheet sheet;
        int rowNum;
        Map<String, Integer> columnMap;

        protected SheetReader(String sheetName) {
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                if (logger != null) {
                    logger.error("workbook:sheet:{}:notFound", sheetName);
                }
                throw new UnexpectedSystemException("WorkbookReader");
            }
        }

        private void validationError(Cell cell, String columnName, String message) {
            if (validationHandler != null) {
                String rowIndex = MDC.get("RowIndex");
                int row = StringUtils.isNotBlank(rowIndex) ? Integer.parseInt(rowIndex) : 0;
                validationHandler.validationError(sheet.getSheetName(), row, columnName, message);
            }
        }

        public SheetReader sheetName(Consumer<String> consumer) {
            consumer.accept(sheet.getSheetName());
            return this;
        }

        public SheetReader header() {
            Row row = sheet.getRow(rowNum++);
            columnMap = new HashMap<>();

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String headerValue = cell.getStringCellValue();
                if (StringUtils.isEmpty(headerValue)) {
                    continue;
                }
                columnMap.put(headerValue.trim(), cell.getColumnIndex());
            }
            return this;
        }

        public <T> RowReader<T> rows(Supplier<T> supplier) {
            return new RowReader(supplier);
        }

        public WorkbookReader end() {
            return WorkbookReader.this;
        }

        public class RowReader<T> {
            final Supplier<T> supplier;
            List<CellReader> cellReaders = new ArrayList<>();

            private RowReader(Supplier<T> supplier) {
                this.supplier = supplier;
            }

            public RowReader<T> binary(String column, BiConsumer<T, Boolean> consumer) {
                cellReaders.add(new BinaryCellReader(column, consumer, true));
                return this;
            }

            public RowReader<T> text(String column, BiConsumer<T, String> consumer) {
                cellReaders.add(new TextCellReader(column, consumer, true));
                return this;
            }

            public RowReader<T> set(String column, String set, BiConsumer<T, String> consumer) {
                cellReaders.add(new FixedSetCellReader(column, consumer, true, set));
                return this;
            }

            public RowReader<T> integer(String column, BiConsumer<T, Integer> consumer) {
                cellReaders.add(new IntegerCellReader(column, consumer, true));
                return this;
            }

            public RowReader<T> number(String column, int scale, BiConsumer<T, BigDecimal> consumer) {
                cellReaders.add(new NumberCellReader(column, consumer, true, scale));
                return this;
            }

            public RowReader<T> integerList(String column, BiConsumer<T, List<Integer>> consumer) {
                cellReaders.add(new IntegerListCellReader(column, consumer, true));
                return this;
            }

            public RowReader<T> timestamp(String column, BiConsumer<T, Timestamp> consumer) {
                cellReaders.add(new TimestampCellReader(column, consumer, true));
                return this;
            }

            public RowReader<T> date(String column, BiConsumer<T, Integer> consumer) {
                cellReaders.add(new DateCellReader(column, consumer, true));
                return this;
            }

            public RowReader<T> rowIndex(BiConsumer<T, Integer> consumer) {
                cellReaders.add(new RowIndexCellReader(consumer));
                return this;
            }

            public CellReaderAdapter cell() {
                return new CellReaderAdapter();
            }

            public abstract class CellReader<T> {
                int index;
                String columnName;
                boolean valueRequired;

                CellReader(String column, boolean optional) {
                    columnName = column;
                    Integer columnIndex = columnMap.get(column);
                    if (columnIndex == null && !optional) {
                        if (logger != null) {
                            logger.error("workbook:sheet:{}:column:notFound:{}", sheet.getSheetName(), column);
                        }
                        validationError(null, String.format("Column not found:%s", column));
                        throw new UnexpectedSystemException("WorkbookReader");
                    }
                    this.index = columnIndex == null ? -1 : columnIndex;
                }

                void validationError(Cell cell, String message) {
                    SheetReader.this.validationError(cell, columnName, message);
                }

                final void read(Cell cell, T row) {
                    accept(cell, row);
                }

                abstract void accept(Cell cell, T row);
            }

            public class CellReaderAdapter {
                boolean optional;
                boolean valueRequired;

                private CellReader init(CellReader reader) {
                    reader.valueRequired = valueRequired;
                    return reader;
                }

                public CellReaderAdapter optional() {
                    optional = true;
                    return this;
                }

                public CellReaderAdapter optional(boolean optional) {
                    this.optional = optional;
                    return this;
                }

                public CellReaderAdapter valueRequired() {
                    valueRequired = true;
                    return this;
                }

                public CellReaderAdapter valueRequired(boolean valueRequired) {
                    this.valueRequired = valueRequired;
                    return this;
                }

                public RowReader<T> end() {
                    return RowReader.this;
                }

                public CellReaderAdapter binary(String column, BiConsumer<T, Boolean> consumer) {
                    cellReaders.add(init(new BinaryCellReader(column, consumer, optional)));
                    return this;
                }

                public CellReaderAdapter text(String column, BiConsumer<T, String> consumer) {
                    cellReaders.add(init(new TextCellReader(column, consumer, optional)));
                    return this;
                }

                public CellReaderAdapter timestamp(String column, BiConsumer<T, Timestamp> consumer) {
                    cellReaders.add(init(new TimestampCellReader(column, consumer, optional)));
                    return this;
                }

                public CellReaderAdapter date(String column, BiConsumer<T, Integer> consumer) {
                    cellReaders.add(init(new DateCellReader(column, consumer, true)));
                    return this;
                }

                public CellReaderAdapter set(String column, String set, BiConsumer<T, String> consumer) {
                    cellReaders.add(init(new FixedSetCellReader<>(column, consumer, true, set)));
                    return this;
                }
            }

            public class TextCellReader<T> extends CellReader<T> {
                BiConsumer<T, String> consumer;
                TextCellReader(String index, BiConsumer<T, String> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    String cellValue;
                    try {
                        if (cell == null) {
                            cellValue = null;
                        } else {
                            cellValue = dataFormatter.formatCellValue(cell, formulaEvaluator);
//                            cellValue = cell.getStringCellValue();
                            if (cellValue != null) {
                                cellValue = cellValue.trim();
                            }
                        }
                    } catch (IllegalStateException e) {
                        double numericCellValue = cell.getNumericCellValue();
                        cellValue = Double.toString(numericCellValue);
                    }
                    if (valueRequired && StringUtils.isBlank(cellValue)) {
                        validationError(cell, "Value required");
                        return;
                    }
                    consumer.accept(row, cellValue);
                }
            }

            public class FixedSetCellReader<T> extends CellReader<T> {
                BiConsumer<T, String> consumer;
                String set;
                FixedSetCellReader(String index, BiConsumer<T, String> consumer, boolean optional, String set) {
                    super(index, optional);
                    this.consumer = consumer;
                    this.set = set;
                }

                @Override
                void accept(Cell cell, T row) {
                    String cellValue;
                    if (cell == null) {
                        cellValue = null;
                    } else {
                        cellValue = cell.getStringCellValue();
                        if (cellValue != null) {
                            cellValue = cellValue.trim();
                            if (set.indexOf(cellValue) == -1) {
                                validationError(cell, String.format("Invalid value:%s:%s", cellValue, set));
                                return;
                            }
                        }
                    }
                    if (valueRequired && StringUtils.isBlank(cellValue)) {
                        validationError(cell, "Value required");
                        return;
                    }
                    consumer.accept(row, cellValue);
                }
            }

            public class IntegerCellReader<T> extends CellReader<T> {
                BiConsumer<T, Integer> consumer;
                IntegerCellReader(String index, BiConsumer<T, Integer> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    Integer intValue = null;
                    try {
                        if (cell == null) {
                            intValue = null;
                        } else {
                            String cellValue = cell.getStringCellValue();
                            if (cellValue != null) {
                                cellValue = cellValue.trim();
                                try {
                                    if (StringUtils.isNotBlank(cellValue)) {
                                        intValue = Integer.parseInt(cellValue);
                                    }
                                } catch (NumberFormatException e) {
                                    validationError(cell, String.format("Invalid number:%s", cellValue));
                                }
                            }
                        }
                    } catch (IllegalStateException e) {
                        double numericCellValue = cell.getNumericCellValue();
                        intValue = (int)numericCellValue;
                    }
                    if (valueRequired && intValue == null) {
                        validationError(cell, "Value required");
                        return;
                    }
                    consumer.accept(row, intValue);
                }
            }

            public class NumberCellReader<T> extends CellReader<T> {
                BiConsumer<T, BigDecimal> consumer;
                int scale;
                NumberCellReader(String index, BiConsumer<T, BigDecimal> consumer, boolean optional, int scale) {
                    super(index, optional);
                    this.consumer = consumer;
                    this.scale = scale;
                }

                @Override
                void accept(Cell cell, T row) {
                    BigDecimal numberValue = null;
                    try {
                        if (cell == null) {
                            numberValue = null;
                        } else {
                            String cellValue = cell.getStringCellValue();
                            if (cellValue != null) {
                                cellValue = cellValue.trim();
                                try {
                                    if (StringUtils.isNotBlank(cellValue)) {
                                        numberValue = new BigDecimal(cellValue).setScale(scale);
                                    }
                                } catch (NumberFormatException e) {
                                    validationError(cell, String.format("Invalid number:%s", cellValue));
                                    return;
                                }
                            }
                        }
                    } catch (IllegalStateException e) {
                        double numericCellValue = cell.getNumericCellValue();
                        numberValue = BigDecimal.valueOf(numericCellValue);
                    }
                    if (valueRequired && numberValue == null) {
                        validationError(cell, "Value required");
                        return;
                    }
                    consumer.accept(row, numberValue);
                }
            }

            public class IntegerListCellReader<T> extends CellReader<T> {
                BiConsumer<T, List<Integer>> consumer;
                IntegerListCellReader(String index, BiConsumer<T, List<Integer>> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    List<Integer> intValues;
                    if (cell != null) {
                        try {
                            String cellValue = cell.getStringCellValue();
                            if (cellValue != null) {
                                cellValue = cellValue.trim();
                                intValues = toIntegerList(cellValue);
                            } else {
                                intValues = new ArrayList<>();
                            }
                        } catch (IllegalStateException e) {
                            double numericCellValue = cell.getNumericCellValue();
                            int intValue = (int) numericCellValue;
                            intValues = new ArrayList<>();
                            intValues.add(intValue);
                        }
                    } else {
                        intValues = new ArrayList<>();
                    }
                    consumer.accept(row, intValues);
                }
            }

            public class BinaryCellReader<T> extends CellReader<T> {
                private String trueList = "true,yes,1";
                private String falseList = "false,no,0";

                BiConsumer<T, Boolean> consumer;

                BinaryCellReader(String index, BiConsumer<T, Boolean> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    String cellValue;
                    Boolean value = null;
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                String svalue = cell.getStringCellValue();
                                if (svalue != null) {
                                    cellValue = svalue.trim();
                                    if (cellValue.length() > 0) {
                                        if (trueList.contains(cellValue.toLowerCase())) {
                                            value = true;
                                        } else if (falseList.contains(cellValue.toLowerCase())) {
                                            value = false;
                                        }
                                    }
                                }
                                break;
                            case BOOLEAN:
                                value = cell.getBooleanCellValue();
                                break;
                            case NUMERIC:
                                double numericCellValue = cell.getNumericCellValue();
                                value = numericCellValue != 0;
                                break;
                        }
                    }
                    if (valueRequired && value == null) {
                        validationError(cell, "Value required");
                        return;
                    }
                    if (value != null) {
                        consumer.accept(row, value);
                    }
                }
            }

            public class TimestampCellReader<T> extends CellReader<T> {
                BiConsumer<T, Timestamp> consumer;
                TimestampCellReader(String index, BiConsumer<T, Timestamp> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    if (cell == null) {
                        consumer.accept(row, null);
                        return;
                    }
                    Timestamp timestamp = null;
                    try {
                        Date cellValue = cell.getDateCellValue();
                        if (cellValue != null) {
                            timestamp = DateUtility.createTimestamp(cellValue);
                        }
                    } catch (Exception e) {
                        String cellValue = cell.getStringCellValue();
                        if (cellValue != null) {
                            cellValue = cellValue.trim();
                            if (cellValue.length() <= 10) {
                                cellValue += " 0:0:0";
                            }
                            timestamp = DateUtility.timestamp(dateTimeLocal(cellValue));
                        }
                    }
                    if (valueRequired && timestamp == null) {
                        validationError(cell, "Value required");
                        return;
                    }
                    consumer.accept(row, timestamp);
                }
            }

            public class DateCellReader<T> extends CellReader<T> {
                BiConsumer<T, Integer> consumer;
                DateCellReader(String index, BiConsumer<T, Integer> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    if (cell == null) {
                        consumer.accept(row, null);
                        return;
                    }
                    Integer date = null;
                    try {
                        Date cellValue = cell.getDateCellValue();
                        if (cellValue != null) {
                            date = DateUtility.date(LocalDate.fromDateFields(cellValue));
                        }
                    } catch (Exception e) {
                        String cellValue = cell.getStringCellValue();
                        if (cellValue != null) {
                            cellValue = cellValue.trim();
                            date = DateUtility.date(dateLocal(cellValue));
                        }
                    }
                    if (valueRequired && date == null) {
                        validationError(cell, "Value required");
                        return;
                    }
                    consumer.accept(row, date);
                }
            }

            public class RowIndexCellReader<T> extends CellReader<T> {
                BiConsumer<T, Integer> consumer;
                RowIndexCellReader(BiConsumer<T, Integer> consumer) {
                    super("", true);
                    this.index = -2;
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    consumer.accept(row, Integer.parseInt(MDC.get("RowIndex")));
                }
            }

            public SheetReader end(List<T> rowValues) {
                return end(rowValues, null);
            }

            public SheetReader end(List<T> rowValues, Consumer<T> finishRowConsumer) {
                try {
                    loggerBegin(logger);
                    MDC.put("Sheet", sheet.getSheetName());
                    while (rowNum <= sheet.getLastRowNum()) {
                        Row row = sheet.getRow(rowNum++);
                        if (checkIfRowIsEmpty(row)) {
                            continue;
                        }
                        T rowValue = supplier.get();
                        rowValues.add(rowValue);
                        cellReaders.forEach(cellReader -> {
                            if (cellReader.index == -1) {
                                return;
                            }
                            Cell cell = null;
                            try {
                                MDC.put("RowIndex", Integer.toString(rowNum)); // Already offset from zero
                                if (cellReader.index >= 0) {
                                    cell = row.getCell(cellReader.index);
                                    if (cell != null) {
                                        MDC.put("Cell", cell.getAddress().formatAsString());
                                    }
                                }
                                cellReader.read(cell, rowValue);
                            } catch (Exception e) {
                                validationError(cell, cellReader.columnName, e.getMessage());
                                logger().error("badCell:{}", e.getMessage());
                                logger().error("cell exception", e);
                                return;
                            } finally {
                                MDC.remove("Cell");
                                MDC.remove("RowIndex");
                            }
                        });
                        if (finishRowConsumer != null) {
                            try {
                                MDC.put("Cell", Integer.toString(row.getRowNum() + 1));
                                finishRowConsumer.accept(rowValue);
                            } finally {
                                MDC.remove("Cell");
                            }
                        }
                    }
                } finally {
                    loggerEnd();
                    MDC.remove("Sheet");
                    MDC.remove("Cell");
                }
                return SheetReader.this;
            }
        }
    }

    public List<String> sheets() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            names.add(workbook.getSheetAt(i).getSheetName());
        }
        return names;
    }

    public SheetReader sheet(int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        return new SheetReader(sheet.getSheetName());
    }

    public SheetReader sheet(String sheetName) {
        return new SheetReader(sheetName);
    }

    static LocalDateTime dateTimeLocal(String date) {
        return DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseLocalDateTime(date);
    }

    static LocalDate dateLocal(String date) {
        return DateTimeFormat.forPattern("dd MMMM yyyy").parseLocalDate(date);
    }

    public static void splitNameIntoPerson(final Person person, String name) {
        String[] split = splitName(name);
        if (split.length == 0) {
            return;
        }
        person.setFirstName(cleanName(split[0]));
        person.setName(name);
        if (split.length == 2) {
            person.setLastName(cleanName(split[1]));
        } else {
            person.setLastName("?");
            logger().warn("emergencyContact:lastName:missing:{}", name);
        }
    }

    public static String defaultValue(String value, String defaultValue) {
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public static void applyPhoneNumber(Person contact, String value) {
        ContactDetails contactDetails = contact.getContactDetails();
        if (contactDetails == null) {
            contactDetails = new ContactDetailsImpl();
            contact.setContactDetails(contactDetails);
        }
        contactDetails.setPhoneNumber(cleanPhoneNumber(value));
    }

    public static void applyEmailAddress(Person contact, String value) {
        ContactDetails contactDetails = contact.getContactDetails();
        if (contactDetails == null) {
            contactDetails = new ContactDetailsImpl();
            contact.setContactDetails(contactDetails);
        }
        contactDetails.setEmailAddress(cleanEmailAddress(value));
    }

    public static void applyState(Person contact, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        Address address = contact.getMailingAddress();
        if (address == null) {
            address = new AddressImpl();
            contact.setMailingAddress(address);
        }
        address.setState(States.valueOf(value));
    }

    public static boolean checkIfRowIsEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0 || row.getFirstCellNum() < 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }
}
