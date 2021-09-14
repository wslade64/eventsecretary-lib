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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

            public RowReader<T> integer(String column, BiConsumer<T, Integer> consumer) {
                cellReaders.add(new IntegerCellReader(column, consumer, true));
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

            public CellReaderAdapter cell() {
                return new CellReaderAdapter();
            }

            public class CellReaderAdapter {
                boolean optional;
                public CellReaderAdapter optional() {
                    optional = true;
                    return this;
                }
                public RowReader<T> end() {
                    return RowReader.this;
                }
                public CellReaderAdapter binary(String column, BiConsumer<T, Boolean> consumer) {
                    cellReaders.add(new BinaryCellReader(column, consumer, optional));
                    return this;
                }

                public CellReaderAdapter text(String column, BiConsumer<T, String> consumer) {
                    cellReaders.add(new TextCellReader(column, consumer, optional));
                    return this;
                }

                public CellReaderAdapter timestamp(String column, BiConsumer<T, Timestamp> consumer) {
                    cellReaders.add(new TimestampCellReader(column, consumer, optional));
                    return this;
                }

                public CellReaderAdapter date(String column, BiConsumer<T, Integer> consumer) {
                    cellReaders.add(new DateCellReader(column, consumer, true));
                    return this;
                }
            }


            public abstract class CellReader<T> {
                int index;
                CellReader(String column, boolean optional) {
                    Integer columnIndex = columnMap.get(column);
                    if (columnIndex == null && !optional) {
                        if (logger != null) {
                            logger.error("workbook:sheet:{}:column:notFound:{}", sheet.getSheetName(), column);
                        }
                        throw new UnexpectedSystemException("WorkbookReader");
                    }
                    this.index = columnIndex == null ? -1 : columnIndex;
                }

                final void read(Cell cell, T row) {
                    accept(cell, row);
                }

                abstract void accept(Cell cell, T row);
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
                            cellValue = cell.getStringCellValue();
                            if (cellValue != null) {
                                cellValue = cellValue.trim();
                            }
                        }
                    } catch (IllegalStateException e) {
                        double numericCellValue = cell.getNumericCellValue();
                        cellValue = Double.toString(numericCellValue);
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
                                intValue = Integer.parseInt(cellValue);
                            }
                        }
                    } catch (IllegalStateException e) {
                        double numericCellValue = cell.getNumericCellValue();
                        intValue = (int)numericCellValue;
                    }
                    consumer.accept(row, intValue);
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
                private String trueList = "true,yes";
                private String falseList = "false,no";

                BiConsumer<T, Boolean> consumer;

                BinaryCellReader(String index, BiConsumer<T, Boolean> consumer, boolean optional) {
                    super(index, optional);
                    this.consumer = consumer;
                }

                @Override
                void accept(Cell cell, T row) {
                    String cellValue = cell != null ? cell.getStringCellValue() : null;
                    if (cellValue != null) {
                        cellValue = cellValue.trim();
                        if (cellValue.length() > 0) {
                            Boolean value = null;
                            if (trueList.contains(cellValue.toLowerCase())) {
                                value = true;
                            } else if (falseList.contains(cellValue.toLowerCase())) {
                                value = false;
                            }
                            if (value != null) {
                                consumer.accept(row, value);
                            }
                        }
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
                    consumer.accept(row, date);
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
                        if (row == null || row.getFirstCellNum() == -1) {
                            continue;
                        }
                        T rowValue = supplier.get();
                        rowValues.add(rowValue);
                        cellReaders.forEach(cellReader -> {
                            if (cellReader.index == -1) {
                                return;
                            }
                            Cell cell = row.getCell(cellReader.index);
                            if (cell == null) {
//                                logger().error("missingCell:{}:{}", row.getRowNum() + 1, cellReader.index + 1);
                                return;
                            }
                            try {
                                MDC.put("Cell", cell.getAddress().formatAsString());
                                cellReader.read(cell, rowValue);
                            } catch (Exception e) {
                                logger().error("badCell:{}", e.getMessage());
                                logger().error("cell exception", e);
                                return;
                            } finally {
                                MDC.remove("Cell");
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

}
