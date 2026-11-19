# Study: "PDF ALL CIRCULAR 2026" (auto-parts price circulars)

Source: the user uploaded `PDF_ALL_CIRCULAR_2026.zip` (13 MB). The raw files are **not** committed here (they are business price lists); this file only records what is in them.

## What it is
A wholesale price-list ("circular") collection from a Pakistani **auto-parts distributor** for 2026.
- **101 files**: 100 PDFs (203 pages in all) and 1 Excel workbook.
- Every PDF was exported from Microsoft Excel (2007/2016), so the text is real text, not scans. `pdftotext -layout` extracts clean table rows, and no OCR is needed.
- Prices are in **PKR**. Each circular has an "EFFECTIVE FROM dd-mm-yyyy" date.
- Footer terms: *"Price can be change any time due to exchange rate"* and *"Delivery in 7 days as per available stock"*. One list adds a 2% discount on a 1-lac order, 3% on a 2-lac order, and no discount under 1 lac.

## Organisation
File names follow `<VEHICLE MAKE> <BRAND> CIRCULAR <date>.pdf`. Some files are named the other way round (`TAIHO TOYOTA ...`), some have typos (`THAIO`, `CIRCULER`, `KTJPCIRCULAR`), and some have numeric prefixes (`1.`, `6.`).

| Group | Brands / product lines |
|---|---|
| Suzuki | ART & SUPAR (pistons), NPR (piston rings), TAIHO (main & big-end bearings), TSK (cables), KGK/KTJP (gaskets, seals), FUJI (valves, suspension), OPTIBELT (belts), AISIN/FCC (clutch), SEIWA (CNG plug wire), GMB (water pump, bearings), KOITO, NEW-ERA, N.DENSO, DAICHI (ignition/electrical), HI-Q |
| Toyota | TAIHO, TSK, NPR, AISIN, ART, FCC, FUJI, GMB, OPTIBELT, SUPAR piston, HI-Q |
| Multi-make | VALEO clutches (Korea/Indonesia), METAL TEC clutches, AISIN U-type clutches, FIC brake kits, MAHLE pistons, KOYO bearings |
| Heavy duty | NPR, TSK, OPTIBELTS, Lucas H.D. valve guide |
| LUCAS U.K. (16.03.26) | C.V joint, spark plug, plug wire, radiator cap, halogen bulb, water pump, Suzuki/Toyota piston, valve guide. Each comes **with price** (dealer + consumer) and **without price** |
| Orders (not price lists) | `NEW ORDER KOYO BEARINGS 03.09.2026`, `AISIN ORDER 03.9.26`, `HI-Q NEW ORDER`: part lists with no prices |

### The Excel master
`1. SUZUKI CIRCULAR 01.05.2025 OK CURRENT CIRCULAR.xlsx` holds **38 sheets**, one per brand (NPR, ART, KGK, TAIHO, TSK, GMB, FIC, NGK plugs, ...). An **INDEX** sheet maps each brand to its page numbers and product type (latest index dated 01.01.26, with an older 15.05.2022 index below it). This workbook is the source the Suzuki PDFs are printed from.

## Typical columns
`S.NO | COMP.N (company code, e.g. HT276) | MODEL (vehicle) | PART NO (OEM/brand) | PACKING | SIZE (STD/0.25/0.50… oversize) | PRICE <date>`
- The Lucas lists add `UNIT | DEALER PRICE | CONSUMER PRICE`. Consumer price = dealer price × 1.15 (for example 6200 → 7130).
- Vehicle names are local market names: Mehran, Ravi, Khyber, Cultus, Margalla, Baleno, Liana, Swift, Alto, Wagon-R, Bolan, ST308, Jimny, Corolla (XLI/GLI, 2D, EE100), Vitz, Hilux (SSR/Vigo), Land Cruiser, Shehzore, Santro, Coure, Pajero.
- Some prices are `N/A` (out of stock).

## Versioning and price trend
The same brand list is re-issued during the year, so the collection is a price history:
- Suzuki TAIHO HT276 (Ravi/Mehran STD): 2320 (23.01) → 2450 (13.03) → 2550 (01.04), then unchanged through 29.08.
- Valeo clutch set TY16/TYC14 (XLI): 9600 (16.02) → 10100 (01.06) → 9900 (01.09). SZ08/SZC04 (Mehran): 9100 → 9300 → 9800.
- Most prices rose in **March–April 2026** (the 13.03 and 01.04 circulars).

## Data-quality notes
- Near-duplicates: `SUZUKI NPR CIRCULAR 2026.pdf` and `...07.05.2025.pdf` both say "effective 07.05.2025". `TOYOTA NPR 14.06.2026` is internally dated 14.05.2026. `TAIHO TOYOTA CIRCULAR 2026` is internally 20.02.2026. Some "2026" files carry 2025 dates (TSK 20.01.2025, Toyota GMB 02.08.2025, Aisin U 01.09.2025).
- `SUZUKI TAIHO 18.05.2026` and `SUZUKI THAIO 18.05.2026` are two layouts of the same date.
- Spelling in the sheets: "EFCECTED", "AXIL", "BIGUN", "DISCRIPTION", "PRESHAR PLATE". A search tool would need fuzzy matching.
- Rows sometimes wrap across 2–3 lines (multiple part numbers or OEM refs in one cell), so a parser has to merge them.

## Possible next steps
1. Parse all PDFs and the xlsx into one normalised table (`brand, make, model, part_no, comp_code, size, packing, price, effective_date, source_file`) as CSV/SQLite.
2. Build a searchable price lookup or catalogue (by model, part no or brand) with the latest price and its history.
3. Make a "latest circular per brand" report and a price-change diff between versions.
4. Produce a clean, combined 2026 master circular (xlsx/PDF).
