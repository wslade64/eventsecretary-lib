package au.com.eventsecretary.client;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeManager {
    private static long TIMEOUT = 1000 * 60 * 60; // one hour
    private int[] index = { 3,7,11,16,22,27,35,49,43,51,53,58};

    private static String[] codes = {
            "0d28320308b1",
            "5a81c5c43868",
            "1a28dacd3333",
            "c5d57a7283c4",
            "5f62c562751b",
            "8da5e7786562",
            "65429d450ef5",
            "e18acbb5103c",
            "01c04691aeab",
            "147c6509b97d",
            "5cf3a563c93b",
            "47fc9ad09a00",
            "58a9e2261769",
            "848fe8fce28a",
            "153be6b3ef28",
            "10dd1c49b475",
            "84e098d42667",
            "67cc460ea1c8",
            "3fecae51e573",
            "6437b9cb8cca",
            "c2d049ce85fc",
            "c06244ef955e",
            "89d53099a9f0",
            "a2d648a68d5b",
            "d7feb395bcee",
            "b633f0893f6d",
            "491446d9e48c",
            "3dfef5c6b012",
            "0b108422d5c7",
            "50c1620be75b",
            "accfd8e9b0ad",
            "282f3c2a8882",
            "dd353201c3de",
            "773a80b5dc8c",
            "d69398cb39a8",
            "88666b8a6204",
            "104af7c15e12",
            "04a68f669da0",
            "258883f91fe9",
            "c2dc5704743c",
            "d6af6c1bce92",
            "fb6069b2612b",
            "32bb9f9db0d1",
            "33986d283a4c",
            "244128626a8e",
            "e1ae3702dfa3",
            "c3dd72984477",
            "594946ed9fe5",
            "0af507d5a10a",
            "8bc01c82408a",
            "b2a5cd782aa0",
            "538e7051f15a",
            "1356f502d0d6",
            "48a63bc74be7",
            "f00e1e2b64e5",
            "6c3aa6cfb61b",
            "f313a88da4c4",
            "d570c2e49c3e",
            "c3703168a513",
            "7a4da9d97ee2",
            "d78376b282d9",
            "16f03bcf6d40",
            "125f1a32a0b8",
            "6114bc4cd25e",
            "c2652033d42f",
            "cb8627b93acb",
            "02b4c2ad5ef7",
            "4cb8846412c7",
            "a89da06fdb0c",
            "dd6069d46306",
            "c24af4f4f932",
            "2ba39a341b6a",
            "cd8de03029af",
            "01cbea9be33d",
            "7b7acf4e90aa",
            "9196deb7c04e",
            "0a15c2fff0b7",
            "542d30dbbb9a",
            "98584b1a4069",
            "bcbacf179390",
            "e2eb2aec8cb8",
            "6bb843fa72b7",
            "9b35f5b2336e",
            "7df1657b644a",
            "e63d3073c393",
            "434274562e31",
            "7ea08be72da2",
            "2e6f94fc74a7",
            "373806822862",
            "4fd1d47c3963",
            "c6501799e6b2",
            "2b61d31a93d4",
            "dded60bb44e4",
            "f42702a19976",
            "d6a62760fa8e",
            "856bb3dc0178",
            "d24c1eb47f72",
            "65ea0a409fc1",
            "855c97404f5e",
            "4fef8dbb6232",
            "9bd4ff18bfb2",
            "ba9fa0f8927c",
            "a3507a2916fe",
            "2cc5cd3c2143",
            "4b877fab779a",
            "3ad5a57ca382",
            "618c32c5680b",
            "7f8dcb544bb7",
            "ee151d14f700",
            "c636fabcadf1",
            "5fc306554577",
            "f17313ec47f7",
            "09886641d7d7",
            "b2599aebf2cb",
            "3ccb67bed801",
            "1f4b112cde65",
            "68b27775c9d1",
            "59f819f0a67a",
            "d48ce65cd9a5",
            "15af07fa2401",
            "1d81badf49de",
            "8c654d687eac",
            "b7e245c099fd",
            "1a08aa7c0425",
            "d9c07caf6fb7",
            "ba707338c7bc",
            "97c07bb67d85",
            "efff3a7c4b8e",
            "2bda369cdc26",
            "aa5bba4c833e",
            "1f806950abf8",
            "4e0c4081d952",
            "86b3a540a080",
            "5cc8c6ca74bb",
            "14fb52a8e063",
            "be6dbc6cdbcf",
            "27bb93958501",
            "ef65d4e1f394",
            "0187277bb01f",
            "401ac933c1c6",
            "bbf2d512278a",
            "d8802594250d",
            "ac14bae1de3f",
            "47dbe9d7bc3e",
            "85ea3892e761",
            "3fd7cd3978ab",
            "e7590bec0e0a",
            "801e0fa3387a",
            "cec02e5a0ebe",
            "0006b0f92ef7",
            "cc57d3ffcf08",
            "6e2dbd0d8c51",
            "a90a96543f25",
            "6145c9c5f894",
            "10f3c145ae14",
            "e66011b3abf9",
            "45295cc84d11",
            "71750d0626b0",
            "bbce62f79092",
            "4bf006c47abe",
            "67efb5748306",
            "656c9a6bf4cd",
            "8116320f2693",
            "098e9d80a7b4",
            "e6ab57d215e3",
            "67d80a8a6489",
            "54d1f8c54eed",
            "38ddaeefdbfa",
            "edbf321757f2",
            "653016d7143e",
            "5280bb0fccc4",
            "4138b70e0e83",
            "da40b829bc59",
            "b992db4b0761",
            "5fffcb596145",
            "917ba5941f49",
            "3f879aac2315",
            "4bb605a1ebfa",
            "62e74ecee8ef",
            "d3d7570d3351",
            "bba6b640956a",
            "dcee5d1f8ff4",
            "8713e20e872e",
            "45e7ab65d42a",
            "ba4c2895a5e8",
            "83402fc0f917",
            "a522678413d4",
            "c0c9e8dde99e",
            "3d0e81362967",
            "caaf1f03318f",
            "b21dfcf208c7",
            "e92644795111",
            "38170e45a3f8",
            "fec20de124f8",
            "ca7cfb4838fe",
            "b0e3d8c2737a",
            "5cdf9e614fe4",
            "b9cc8a032937",
            "3e72017461fd",
            "ccc3a4bd5d6c",
            "29088c6fc3ea",
            "9dd4a0de056c",
            "126abbf54581",
            "79217e93b7f6",
            "1a3345e23a41",
            "0c193a469661",
            "85211ad344a2",
            "383cf2875429",
            "44ce9c01d528",
            "a321b0e2f2b0",
            "de5927608586",
            "b8084d518fc2",
            "5b2c64d40811",
            "89446dd54877",
            "ef7c3bc16f8b",
            "cc51d4cc3530",
            "93d574f0632e",
            "149effb7d363",
            "365ab41ee391",
            "afc9eac44d81",
            "5fecbb1bbc5b",
            "e5e2de01ebd2",
            "1c3e7237eb86",
            "ccbc6ee66adc",
            "ba1cf17ce334",
            "8a9be98d299c",
            "88ba8fafa87b",
            "f7f34a2113d5",
            "cfb93db98be2",
            "d145c3d35ba2",
            "1253870a8bc2",
            "5ad202f793a8",
            "66cbc2bf2030",
            "2ed79a532b74",
            "c0e328985c8b",
            "0cf64425a3a5",
            "3553ed3fc35a",
            "b65d7d903426",
            "7d10e99dbd9f",
            "4849fbf9aeae",
            "ca8ad2f14ec2",
            "05f12847d2d6",
            "a034fd1ce3ff",
            "5af0be920ba0",
            "bf6bbbb9ca06",
            "ebe3d6406b32",
            "1e540b585e78",
            "1fde1bf2fbc2",
            "368dee844db6",
            "9031542bcfca",
            "094cd904cd11",
            "e41c55018747",
            "63f6a01efe4d",
            "00d3eee283cc",
            "686cffd4d0a6",
            "946816537091",
            "0b13090001e2",
            "e3085a8376d5",
            "3ecaf4edaacb",
            "75f3966cfbcf",
            "29a292697b32",
            "363d66dcd2a8",
            "b6db9b6ff439",
            "4412d0d0c8f2",
            "024d105730e4",
            "d29488c6e854",
            "760883bda057",
            "a46520237ecd",
            "6c6ee73a8cca",
            "75b3e55eb8ec",
            "c767c251a40d",
            "1d08968d96dd",
            "d00d00e70670",
            "8622db45f019",
            "f09fad03cb1e",
            "086f4f1a6dbe",
            "af9cde3f2540",
            "17dc579f2788",
            "d788bede2192",
            "a876889e84c3",
            "c889c3d5abca",
            "9c1aa932dcb3",
            "aa884872913d",
            "84db74b0dbaf",
            "7a2a2ff1854d",
            "83d1d2f58159",
            "ddf71836cfb7",
            "86c2a69ffa68",
            "928c925fdca9",
            "cf672e5061a4",
            "dd3ca7a421a6",
            "504ff6c718a1",
            "7270b863aec2",
            "398189ef941c",
            "7b0b45552d70",
            "aea74527ddb9",
            "45a726159b9f",
            "717f3e33149f",
            "383c27c2485c",
            "b8473f449aec",
            "5e7f5d4621de",
            "bffe5905c157",
            "588e68835146",
            "6f644e3328fb",
            "63a6ee972e94",
            "f934a466ac54",
            "914d44c119d1",
            "2980b930a778",
            "109c74a33c2c",
            "2251b0d16113",
            "9953ae01666e",
            "70c8e5e5f5f9",
            "8e9e84645f37",
            "21c8fe4e7872",
            "525a18c0df2d",
            "b14c7e5e5fcd",
            "0337bede0798",
            "c1d55d2344fe",
            "464184ad1760",
            "18370c6fe304",
            "555ed476696c",
            "0593bb88fcac",
            "3f645a117cad",
            "089ede8b7a6a",
            "31a682e79839",
            "e1668382ac8a",
            "12b25cc4173e",
            "45f646913508",
            "a37368911f5b",
            "d940f3697682",
            "927ce2871bd2",
            "ffd6e238abc0",
            "2d5f446ea95f",
            "03e2331a4148",
            "540079cbd449",
            "e18a945529d4",
            "77082aaf76db",
            "003bc5c46bfc",
            "67c670e429d6",
            "5c7c225cc9f4",
            "6db1d6341326",
            "6945964e0b17",
            "d281eced639f",
            "89f6c44f2120",
            "d795d9a6c97d",
            "c374700ef6e9",
            "1a2da2958c0a",
            "4b890060b394",
            "efe04571751b",
            "878d5d18a5a9",
            "bc83956c8fbe",
            "77b753bdbe5c",
            "5aa6764a2b4c",
            "2ce92b097404",
            "5967f44ab500",
            "140b92c39dea",
            "1390988d80ca",
            "eea0ce3610af",
            "2e95c5969af0",
            "72663b6cc125",
            "c178d3642928",
            "50a0d4baa036",
            "9a7a49281521",
            "d8adcf3e6c01",
            "29f5d98176a8",
            "7f272f52659e",
            "b213d858d363",
            "e17b2fef250c",
            "577314b285b0",
            "cdd50b1d7907",
            "897cdea0362b",
            "60d25fada22a",
            "0a0ec3ec82a9",
            "99c4d33fff7a",
            "d734a3cb122a",
            "4dec28612836",
            "ce8b62cf01b0",
            "58453eb3f17d",
            "e2ace4557b3f",
            "f73cc157fca3",
            "91e4eea493e4",
            "461ec9daa77c",
            "37e2cfe8476b",
            "fa12ca5604af",
            "7c98694400a5",
            "df66d55028c2",
            "6b9f67124a53",
            "3d86fb2d170c",
            "b437e3bff266",
            "896efc39f05e",
            "e0feaa8f6eae",
            "fcf8e3793bc2",
            "8b89ab93b6c6",
            "4cfa0542a630",
            "fcbcced65ba6",
            "dc72aa3f3ebc",
            "744c3dea664a",
            "0c00d42660c7",
            "61ddd954ade9",
            "c7e2dd232cf7",
            "03af016d5d24",
            "d548760f12dd",
            "1791170c8b0e",
            "463226071a5d",
            "69bb80d0bc03",
            "4589f3b5cb38",
            "57d5c732eae0",
            "1e76045de842",
            "0c6bdda2090d",
            "679bbb0e8e3c",
            "6315680da1af",
            "c2f99dc213f3",
            "9cfb399f864b",
            "949ff00339d9",
            "f6ec1b2410e6",
            "7b8d8a2506f1",
            "00e06bb39422",
            "ddc56bdb365e",
            "1580d3d7c774",
            "6ad9c7d531b9",
            "34597529c48c",
            "f3c54a6fc9a8",
            "81daa5547a55",
            "90b3082cacfe",
            "b6036be25337",
            "216f66f82d58",
            "9b283ee3164a",
            "8617614c9142",
            "c563b8d2cc38",
            "e31ee3ddef07",
            "a3dab4e7440d",
            "3b42627e8f3c",
            "85bedb0192b3",
            "614f790da50b",
            "515eb4edf3af",
            "4f6b48893ac3",
            "2a098627fd1e",
            "8e223511fecb",
            "308ccd0a28ec",
            "37777de3a89f",
            "23e878d92d7c",
            "13fbe3708a56",
            "67add1d0a97d",
            "e98517e67a3e",
            "1ebf8eb08ea6",
            "0ad46cecb115",
            "8d2c728c81c1",
            "fcf52b850b51",
            "fb42e5f6a3ff",
            "c5dd47386196",
            "429ec4a9a4ac",
            "75c62e03b5ea",
            "51d1440d8562",
            "6fe9a8e3ba46",
            "d53454bb9200",
            "b458f922aecd",
            "eea3683d2fda",
            "c8972bf92a6b",
            "747dd9118c82",
            "b5f0bfaf4367",
            "8f3605593488",
            "f6ce2b22ab14",
            "9c8e99e82add",
            "8554a134fe06",
            "4f2798e086e3",
            "fb9b8f29dfe1",
            "9fb2aea5e1a4",
            "92017e42c894",
            "471c5e13c858",
            "91749cb6681a",
            "4c0709fcb6c5",
            "8be1987ab822",
            "6dd3b9142321",
            "6d3571959ecd",
            "4d0acf172321",
            "bda9dd0072e0",
            "b6fdc63d7f98",
            "7c0314fe18fb",
            "a624021b8eb3",
            "3b995137c579",
            "ce130adf2d90",
            "95af6c2bef7e",
            "e9abaea568f1",
            "b78ae1328344",
            "71a7e2d46c59",
            "12e4473d54c0",
            "8f334f748834",
            "24e94cbca32a",
            "df46ff9187a9",
            "1778cd99ad74",
            "69274db11c08",
            "48a7df38fc1b",
            "cd3adc503587",
            "3b35f657b308",
            "4eee9c5912d0",
            "a31ce62c085e",
            "7fa3e8696f06",
            "c8fcd29f0786",
            "6313a56981c3",
            "3acfb0371301",
            "ea0f7a57821b",
            "322908cc0882",
            "5380b39b0087",
            "a227bcb8d4c2",
            "c1ef0937f53c",
            "e64f1e77b0d8",
            "27785b8b95af",
            "a1a0ccaee59a",
            "cdfd86d31810",
            "74f538de7de0",
            "2490c4bef5f5",
            "467b7bdaea1a",
            "0966c7559374",
            "dd410f248225",
            "3ce342e1c322",
            "ccca7f29bbdc",
            "142d34db02c9",
            "9db47517b307",
            "fa4732ef3529",
            "2daf74fbab99",
            "6c3cb4ee0974",
            "b639b472e73a",
            "ac7361f5a95e",
            "0fb08d0126fa",
            "8ff1456868dc",
            "8357d4845f60",
            "2f42fa692436",
            "d659971568bc",
            "32e29f774b65",
            "2dba08031170",
            "6d67c3a25cce",
            "a36818fa5bd5",
            "22a44be876cb",
            "5b196938d5df",
            "0aca78f43529",
            "d59b6f293b73",
            "6f5db1f2db2b",
            "f9dd4e2741e1",
            "8a965159c776",
            "2a0ba208fa85",
            "1022aeeac181",
            "c50a2faee696",
            "1b6cfa4ec4c1",
            "9c417f564afc",
            "cdcad99db7c0",
            "4f4a4facb032",
            "4857011c8d54",
            "bbb39a29f8e5",
            "186f3aeb523d",
            "896d7eb46569",
            "0e7c6766771e",
            "6fa8ae84776e",
            "2fe4b917c085",
            "f6286c1f61eb",
            "0290edb3dede",
            "43f20afae622",
            "4d609af63083",
            "f03827ec4602",
            "ee92807e2320",
            "cf644debddcc",
            "6c9c27b0deb4",
            "c15dde564224",
            "500297715aa0",
            "cb6065843625",
            "12a69998d859",
            "76ee0ef80e9b",
            "57a151433340",
            "68b13619dfed",
            "90b2dfe38adf",
            "020735d5f891",
            "1616ccbfb723",
            "f9b193c06bff",
            "a8735e27218a",
            "ea50d2da251e",
            "653791cf3c0d",
            "888e8b0e65d9",
            "58f2233dac0d",
            "ee02554a30e9",
            "fca7989f2774",
            "b47052df6a1e",
            "c1321498f305",
            "368b456dc5e4",
            "b073722a4401",
            "84d22341e2b0",
            "6d8de48119f0",
            "9c3e401c2c68",
            "49b823ca280b",
            "505cd70da0c4",
            "402764748762",
            "ac033fa5ab3b",
            "c46ce68fd08e",
            "5cb0210b2105",
            "d8eb93571be9",
            "f293ad78ec01",
            "f43efa742cbb",
            "abbd8446dfd5",
            "cc24545afa2c",
            "6ca2c28e46c9",
            "00c5620f006e",
            "11d8f2f73d46",
            "01570b43bfa7",
            "1387f694b858",
            "8cf719acb6f0",
            "b8c6fb311dec",
            "2c745a7d23df",
            "45b80747fdfc",
            "8c2f14c1adb9",
            "3409e165cbf1",
            "4e398af33eee",
            "5e7ddaa260e6",
            "1354a76da35e",
            "17be5258b334",
            "050ecba6349e",
            "3ee52b619a5d",
            "905b30e010ee",
            "23399c09fe1e",
            "e206c5f1cdd4",
            "b85fb5d55347",
            "8416a948678c",
            "8b55c750519d",
            "747f0acfd9f4",
            "dd3bb3e80342",
            "296efb2b9c8a",
            "184e4b2df3c8",
            "6f6e73b973fa",
            "bd2c94e5e517",
            "15e797d8665e",
            "8f1393771c16",
            "57d6bdb4016d",
            "0331b0413ea6",
            "34e7f2720619",
            "8faefd59a06e",
            "695bba66a9be",
            "bcb4dc7611a0",
            "13171204c0dc",
            "ba2364df3ffd",
            "8a051948f3da",
            "fbada1bfce2c",
            "39f368a94800",
            "1c790b953c59",
            "c67d46e964b0",
            "e535e195c578",
            "80d4c73d3756",
            "57ad042348d5",
            "fa68ca76a87a",
            "bd63755e76c9",
            "2c7bf8a854d3",
            "37d9b9be8243",
            "d14926823974",
            "7aa5a0847978",
            "68d77302cc5f",
            "00dd8debb42e",
            "1ebb1bcb271f",
            "f95e97fb583c",
            "b5da7046df39",
            "fe9cca4caab5",
            "d0552139309f",
            "f6ec01ccf1ab",
            "2552199602c8",
            "0c3e58b077f7",
            "ea021dea694b",
            "067021f747ab",
            "2a3353379a28",
            "955dc1acae21",
            "eaf5234d304e",
            "dc7321ee132f",
            "455a6d0f9999",
            "87a88e39f1b0",
            "e25559f085cf",
            "b0c2a6d21460",
            "107c4ecca6fc",
            "e589117c4d30",
            "40670b5254bb",
            "208715ffeccf",
            "07edf3f81854",
            "1ab3a0329c64",
            "fe51f2c38d4f",
            "8ebea5e42109",
            "fefb3566a95c",
            "b7af56a889ff",
            "654fbd33d981",
            "4103a5a986e5",
            "5ecef2434637",
            "5ab54394168d",
            "68200becfb6f",
            "1bc76e55fac9",
            "78883d024f8d",
            "c5e6db6384fb",
            "769d2653f7f6",
            "871a5f6f09cc",
            "f309b8e5aeca",
            "e198c3738c43",
            "dfd63d639b49",
            "6004513f5bbd",
            "e010f58ad717",
            "345bb320553c",
            "c08dc208646f",
            "b65e63dec164",
            "ece8ccbf1371",
            "9b9362989e6c",
            "b428208aacec",
            "6ba8597258a9",
            "9d7d81069829",
            "4ef278850869",
            "dd435eefb083",
            "0cf1965e6981",
            "7f3203a8f753",
            "8e0394986a17",
            "c60ee0e11f0a",
            "f12f9e9b063c",
            "8fbbf9f32c67",
            "53adb46d6e3a",
            "750382cf379a",
            "751a14f59a6c",
            "4b0aaabe5573",
            "d9a6629e3096",
            "691cd2e1fede",
            "376ef4926edd",
            "af54bfd52f0d",
            "faa0877c58c2",
            "bee1823ae19c",
            "53cc8eafa74a",
            "f4b5ba1a956e",
            "d21ff9bca8e4",
            "bc93faba2297",
            "bd3fdf2c3ca4",
            "34c319e2f5bf",
            "35991aec09eb",
            "17714a834ed7",
            "006e35407b78",
            "4ba7465a0f7e",
            "0634750be98e",
            "5f5404f6e335",
            "5ff78f186c1e",
            "e4b8a3104f3c",
            "6a8eb60554d9",
            "5cccff0eaf31",
            "efefe407ae6b",
            "98a7820d3fa2",
            "1ebe4d55a2d8",
            "9f42e504d5f9",
            "09b6d63ff98b",
            "bb21e2563694",
            "2fd9983f960f",
            "8962ec2fb248",
            "cc46a0fdf789",
            "3a9d6b0a85bc",
            "d09b307c4b0f",
            "6ad187aff86d",
            "b521a5462586",
            "89cbab93fd3d",
            "dfd3f0625adf",
            "27e1d93126d9",
            "6bf9e31dd260",
            "e63a624b9e41",
            "2984bcff77aa",
            "0b38e9a986ca",
            "69d7fdaa9989",
            "46f5a9eb021a",
            "2a80be434cae",
            "0f09792fe509",
            "ab3e05713b88",
            "fec3ff9c1b7c",
            "b47e53f109cf",
            "327d39cc4af5",
            "81f286be82c2",
            "1e5e223b9d9f",
            "596dd4cb26f5",
            "7bc472649765",
            "21cecc668fa5",
            "8572b67be0aa",
            "7f142f68cfd5",
            "586fa082666c",
            "d35e25645812",
            "47840c26a3b3",
            "5a5d9110b99f",
            "2b8f3c369f83",
            "8b3fab4e2c0d",
            "bf9829f44d14",
            "a401448af50c",
            "869681088e91",
            "b49130bb3ce2",
            "dfdf5aef92fc",
            "0e64b905a0d8",
            "296ef93340b8",
            "919bd0ec57c3",
            "5e07243506c4",
            "778968ed3d9a",
            "702014180729",
            "289d1e4c0a15",
            "4b2a32e54df0",
            "533a93affb0d",
            "1888f098b619",
            "29269f8d38c4",
            "b891579c12f7",
            "03de6c1944ce",
            "01a51539be8d",
            "334a6e5c7f01",
            "2120aa447b92",
            "de585f163b84",
            "2b2e4e45aa19",
            "df0a62574e30",
            "4c7aacfefbc4",
            "33863a5cdf48",
            "e12ab062a02d",
            "b99c9e6ba7a5",
            "3a28ab65afb1",
            "a48ff4170a38",
            "05ff4ddb0e62",
            "9d7a381402a9",
            "9111a046ae11",
            "6ace7c48b358",
            "94720de4f99d",
            "da4b3c6e1636",
            "a80bc8cf821f",
            "1729b5c60792",
            "3506cf021212",
            "97cf5e9feb6b",
            "ef326d718af5",
            "6078901ab7fb",
            "7a93c9e3222c",
            "d2e3f1662da1",
            "2d56202b4d85",
            "961e8df946a4",
            "9d41359262b5",
            "91db619837e8",
            "0121189709a4",
            "2611739b5432",
            "66db3c69bb73",
            "59891911706b",
            "bf967873e7aa",
            "b5f6dea8b1d3",
            "cfde29494405",
            "6fff3b5642cb",
            "c915f8bd6321",
            "e5539c79002d",
            "85accc2d843a",
            "c994db680602",
            "e54ac2c3c96b",
            "5a7e4e8f4e61",
            "3713ade88494",
            "429841feae9b",
            "a7f89dfe580c",
            "1e96a3cd9090",
            "49ae602a6f49",
            "254953666883",
            "cf032a080614",
            "6006d07b61c8",
            "efe099c017b5",
            "837246f1ce8d",
            "a00f46ecfa30",
            "a5604f368bda",
            "ac6eaf6a3d22",
            "ed9286dee68e",
            "bba28a225212",
            "6e4fb0d862ae",
            "61d9110ec9a7",
            "3c405da73e5d",
            "b7109b4e094c",
            "d42aa66901c3",
            "d2b0cebd1f6e",
            "6a806d324e7b",
            "6567c2aa3994",
            "68497702b089",
            "80d48c6705f2",
            "676da1015d11",
            "1fe71f214f4c",
            "4283e4b23438",
            "b6ae72dabee6",
            "ed392ac4fedf",
            "0345dee8f8ab",
            "cc6d076dedc0",
            "b3505957e8fa",
            "e73eb0452cb3",
            "830f70f9a495",
            "a3f77283a9fb",
            "47a130008c80",
            "9a984a36969b",
            "97c85a9fc680",
            "88a14aedf403",
            "422e4f9a09e6",
            "4d3139ddc026",
            "bd8161f3c39e",
            "f748fb57a18a",
            "7c96010f6e52",
            "399d00d30d42",
            "611d24265fff",
            "bea17dced8eb",
            "399119b1da1e",
            "d486e3401d04",
            "f94a33e60687",
            "99c9ae91aaf4",
            "235dc0f6d21c",
            "a525d68f8014",
            "19a9feb3d9ac",
            "28b2493a8e16",
            "173e6c2111fa",
            "04b93836bc5a",
            "5c55624e396e",
            "a9c147871674",
            "5c483636c49c",
            "a32ea400a173",
            "a14ea7eaa574",
            "20cfa2ae7c59",
            "b8d2b487ab94",
            "e73edd254f18",
            "0cd06855d295",
            "65753705c80e",
            "6138a0ede8e0",
            "bcd6abad4d16",
            "db73f4adede9",
            "aed0a15ae5e2",
            "874b7d5ceaad",
            "e7d80b3c3181",
            "e2112ce42048",
            "925e9f34c596",
            "c7971f17dd09",
            "2a562060c0e0",
            "f0b3ebd8dd89",
            "813e9ef236bf",
            "72295e243a37",
            "f6abe29e2625",
            "6db8417546c0",
            "02f2c235e46c",
            "a74e793c4974",
            "ab5a8f547660",
            "589c3a0a8014",
            "bfbed856a648",
            "a50c67b02bb7",
            "628627be49b2",
            "a4b25094f7fc",
            "afc3594629be",
            "0cf300d27541",
            "8a0364113391",
            "d5d2b916c7e3",
            "66ba8a66f7ae",
            "2f0d980f8f19",
            "9ab5b3c48af1",
            "1e09fd364c4b",
            "f35dce2270de",
            "03c148a742b0",
            "99d122f39570",
            "77453733c6c8",
            "6d60953e9f27",
            "3f6d452eef0f",
            "79cdc043b1b3",
            "8b536184e9a4",
            "e8deb746a119",
            "190bea723feb",
            "31b3a2356c37",
            "8aca15590cb4",
            "b1d18972482f",
            "22032c68ac7e",
            "460397952288",
            "4e923f3cdc8a",
            "ce779e0502c4",
            "86868b43e2a1",
            "6861171985d5",
            "93011ea2b5a4",
            "933e6430968d",
            "f06495dc6764",
            "01bd3093315a",
            "7689b3909c72",
            "3e0ccfdb0093",
            "e4a9f7470f5a",
            "b6afc9ed49e4",
            "a636a9830bf1",
            "6949ec09bc37",
            "efa490361837",
            "a0e883ff9b36",
            "431f6ea126af",
            "a35541bd5b12",
            "5703afcb9f04",
            "4dc16a6651f7",
            "e825fecab574",
            "1d8a18add0a1",
            "808d59652e84",
            "aaa8f004959d",
            "f6e1ab347066",
            "f6c8e2aa29a4",
            "35f86ec1141b",
            "5f55c98f3d69",
            "d5cd080d1de5",
            "95a044ef7358",
            "7ddd0720cb2b",
            "0c312398e405",
            "241dd30f2de1",
            "44a352619c7e",
            "b5de5b013ef3",
            "77a9cbde3778",
            "80f22de578c6",
            "8e2407e621b1",
            "7c79d1e39490",
            "285a579a77e1",
            "a8eb1aa1e7b4",
            "b9e7a51dc05e",
            "a24a900b5571",
            "9a3a8f561b76",
            "1ca6323e5bc3",
            "fa0eb4b1168d"
    };
    private static class KeyAccess {
        String code;
        long time;
        public KeyAccess(String code) {
            this.code = code;
            time = System.currentTimeMillis();
        }
    }

    private HashMap<String, List<KeyAccess>> keyCache = new HashMap();

    public synchronized boolean checkCode(String key, String code) {
        code = extractCode(code);
        if (code == null) {
            return false;
        }

        if (!validCode(code)) {
            return false;
        }
        synchronized (this) {
            List<KeyAccess> keyAccesses = keyCache.get(key);
            if (keyAccesses == null) {
                keyAccesses = new ArrayList<>();
                keyCache.put(key, keyAccesses);
            }
            expirerCodes(keyAccesses);
            for (KeyAccess keyAccess : keyAccesses) {
                if (StringUtils.equals(keyAccess.code, code)) {
                    return false;
                }
            }
            KeyAccess keyAccess = new KeyAccess(code);
            keyAccesses.add(keyAccess);
            return true;
        }
    }

    private void expirerCodes(List<KeyAccess> keyAccesses) {
        long now = System.currentTimeMillis();
        keyAccesses.removeIf( k -> now - k.time > TIMEOUT);
    }

    private boolean validCode(String code) {
        for (String s : codes) {
            if (StringUtils.equals(s, code)) {
                return true;
            }
        }
        return false;
    }

    private String extractCode(String code) {
        if (StringUtils.length(code) != 64) {
            return null;
        }
        char[] chars = new char[index.length];
        for (int i = 0; i < index.length; i++) {
           chars[i] = code.charAt(index[i]);
        }
        return new String(chars);
    }
}
