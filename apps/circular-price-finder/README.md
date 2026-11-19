# Circular Price Finder

A browser app for searching the 2026 auto-parts price circulars by model, part number or brand. It shows the latest PKR price, how it changed from the previous circular, and the full price history.

Live (private) link: https://claude.ai/artifact/AKvZhLrg7rCKKHo6yrhVAo

## Rebuild the data
`data.json` is **not committed** because it is business price data. To rebuild it, unzip the circulars and run:

```
pip install pdfplumber openpyxl      # pdftotext (poppler) is also needed
python3 -I build_data.py "<folder with PDFs + xlsx>" data.json
```
Put `data.json` next to `index.html` and serve the folder (`python3 -m http.server`), or republish the artifact.

## How the data is built
- `pdfplumber` reads the Excel-exported tables, and `openpyxl` reads the master workbook (one sheet per brand).
- Files whose names contain "WITHOUT PRICE" are skipped.
- The same part across different circular dates is grouped by brand plus its code, model, size and description fields. The newest dated version is shown, and order lists never count as the latest version.

## Deploy to a VPS (Ubuntu/Debian)
Folder layout: `install.sh` next to `site/` (`site/index.html` + `site/data.json`; the standalone index has its own `<head>`). Run `bash install.sh` for IP access, or `bash install.sh your-domain.com` to add free HTTPS via certbot.
