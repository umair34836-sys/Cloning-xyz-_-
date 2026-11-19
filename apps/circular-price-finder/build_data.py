import sys, os, re, json, datetime, warnings
warnings.filterwarnings('ignore')
import pdfplumber, openpyxl
src=sys.argv[1]; out=sys.argv[2]
DATE_RE=re.compile(r'(\d{1,2})\s*[-./]\s*(\d{1,2})\s*[-./]\s*(\d{2,4})')
def pdate(s):
    for m in DATE_RE.finditer(s or ''):
        d,mo,y=map(int,m.groups())
        if y<100: y+=2000
        try: return datetime.date(y,mo,d).isoformat()
        except: pass
    return None
BR=[('THAIO','TAIHO'),('TAIHO','TAIHO'),('TSK','TSK'),('NPR','NPR'),('SUPAR','SUPAR'),('ART','ART'),('AISIN','AISIN'),('FCC','FCC'),('FUJI','FUJI'),('GMB','GMB'),('OPTI','OPTIBELT'),('HI-Q','HI-Q'),('KGK','KGK'),('KTJP','KTJP'),('SEIWA','SEIWA'),('KOITO','KOITO'),('NEW-ERA','NEW-ERA'),('DENSO','DENSO'),('DAICHI','DAICHI'),('VALEO','VALEO'),('METAL TEC','METAL TEC'),('FIC','FIC'),('MAHLE','MAHLE'),('KOYO','KOYO'),('LUCAS','LUCAS')]
def meta(fn):
    u=fn.upper()
    make='HEAVY DUTY' if 'HEAVY' in u else 'SUZUKI' if 'SUZUKI' in u else 'TOYOTA' if 'TOYOTA' in u else 'MULTI'
    brand='OTHER'
    for k,v in BR:
        if re.search(r'(^|[^A-Z])'+re.escape(k), u): brand=v;break
    return make,brand
def clean(c):
    if c is None: return ''
    if isinstance(c,float) and c==int(c): c=int(c)
    if isinstance(c,(datetime.datetime,datetime.date)): c=c.strftime('%d.%m.%Y')
    return re.sub(r'\s+',' ',str(c)).strip()
SN=re.compile(r'^(S\.?\s?N\.?O?|SR\.?\s*NO|SR|S\s?#|NO|P\.?N)\.?$',re.I)
def is_header(cells):
    ne=[c for c in cells if c]
    return len(ne)>=3 and (SN.match(ne[0]) or any(re.search(r'PART|MODEL|DESCRIPTION|DISCRIPTION',c,re.I) for c in ne)) and not re.match(r'^\d+\.?$',ne[0])
def toint(s):
    s=s.replace(',','').strip()
    m=re.match(r'^(?:RS\.?\s*)?(\d{2,7})(?:\.0+)?(?:/-)?$',s,re.I)
    if m:
        v=int(m.group(1)); return v if v>=20 else None
    return None
PRICEH=re.compile(r'PRICE|PPRICE|RATE|DEALER|CONSUMER|^\d{1,2}[./-]\d{1,2}[./-]\d{2,4}$|^RS',re.I)
files=[];rows=[]
import subprocess
def hdr_date(label):
    p=os.path.join(src,label)
    if not os.path.exists(p) or not p.lower().endswith('.pdf'): return None
    txt=subprocess.run(['pdftotext','-layout','-l','1',p,'-'],capture_output=True,text=True).stdout
    for l in txt.split('\n')[:10]:
        d=pdate(l)
        if d: return d
    return None
def process(fn_label, make, brand, kind, tables_iter, file_date):
    fid=len(files)
    info=dict(file=fn_label,make=make,brand=brand,kind=kind,date=None,rows=0)
    files.append(info)
    hdr=None; section=''; prev=None; dates=[]
    for cells in tables_iter:
        cells=[clean(c) for c in cells]
        ne=[c for c in cells if c]
        if not ne: continue
        if is_header(cells):
            hdr=cells; prev=None; continue
        first=ne[0] if cells[0]=='' and len(ne)==1 else cells[0]
        if len(ne)<=2 and not re.match(r'^\d+\.?$',cells[0] or 'x'):
            t=' '.join(ne)
            d=pdate(t)
            if d and re.search(r'EFF|DATE|FROM|DATED|EFCECT|\d',t,re.I): dates.append(d)
            if re.search(r'PRICE CAN|DELIVERY|DISCOUNT|^\*',t,re.I): prev=None; continue
            if not re.search(r'CIRCULAR|CERCULAR|EFFECT|EFCECT|^DATE|^NOTE|PRICE CAN|DELIVERY',t,re.I) and len(t)<80 and not d:
                section=t
            # continuation row with 1-2 cells
            if prev is not None and cells[0]=='' and not d and len(t)<60 and hdr:
                for i,c in enumerate(cells):
                    if c and i<len(prev['c']): prev['c'][i]=(prev['c'][i]+' / '+c).strip(' /')
            continue
        if re.match(r'^\d{1,3}\.?$',cells[0]):
            r=dict(f=fid,c=cells,sec=section,h=hdr)
            rows.append(r); prev=r; info['rows']+=1
        elif cells[0]=='' and prev is not None:
            for i,c in enumerate(cells):
                if c and i<len(prev['c']):
                    if toint(c) and not toint(prev['c'][i] or 'x') and prev['c'][i]=='' : prev['c'][i]=c
                    elif prev['c'][i]!=c: prev['c'][i]=(prev['c'][i]+' / '+c).strip(' /')
    info['date']=dates[0] if dates else (hdr_date(fn_label) or file_date)
for fn in sorted(os.listdir(src)):
    u=fn.upper()
    if not u.endswith('.PDF') or 'WITHOUT PRICE' in u: continue
    make,brand=meta(fn)
    kind='order' if 'ORDER' in u else 'list'
    def it():
        with pdfplumber.open(os.path.join(src,fn)) as pdf:
            for pg in pdf.pages:
                for tb in pg.extract_tables():
                    for r in tb: yield r
    process(fn,make,brand,kind,it(),pdate(fn))
for fn in sorted(os.listdir(src)):
    if not fn.endswith('.xlsx'): continue
    wb=openpyxl.load_workbook(os.path.join(src,fn),data_only=True)
    for ws in wb:
        if ws.title=='INDEX': continue
        make,brand='SUZUKI',ws.title.upper()
        process(fn+' › sheet '+ws.title,make,brand,'master',(list(r) for r in ws.iter_rows(values_only=True)),None)
# normalise rows -> fields + price
out_rows=[]
for r in rows:
    F=files[r['f']]; h=r['h'] or []; c=r['c']
    fields=[];prices=[];dealer=cons=None; na=False
    pcols=[i for i,x in enumerate(h) if x and PRICEH.search(x)]
    for i,v in enumerate(c):
        if i==0 or not v: continue
        hn=h[i] if i<len(h) else ''
        if i in pcols:
            n=toint(v)
            if n: 
                if re.search(r'CONSUMER',hn,re.I): cons=n
                else: prices.append((hn,n))
            elif re.match(r'^(N/?A|NA|-+)$',v,re.I): na=True
            continue
        if not pcols:
            pass
        fields.append([hn,v])
    if not pcols:
        # fallback: rightmost integer >=20 that is not part-number-like
        for k in range(len(fields)-1,-1,-1):
            n=toint(fields[k][1])
            if n and not re.search(r'NO|PART|SIZE|PACK|QTY|STOCK',fields[k][0],re.I):
                prices.append((fields[k][0],n)); fields.pop(k); break
    o=dict(f=r['f'],s=r['sec'],x=[[a,b] for a,b in fields])
    if prices:
        o['p']=prices[-1][1]
        if len(prices)>1: o['old']=[[a,b] for a,b in prices[:-1]]
    if cons: o['cp']=cons
    if 'p' not in o and (na or True): o['na']=1 if na else 0
    out_rows.append(o)

SKIP=re.compile(r'PRICE|RATE|DEALER|CONSUMER|PACK|PCK|QTY|AVAILABLE|STOCK|^\d{1,2}[./-]',re.I)
def key(o):
    F=files[o['f']]
    KEEP=re.compile(r'PART|COMP|MODEL|MODAL|SIZE|DESC|DISCRIP|BEARING|FITMENT|ART NO|OPTI|BANDO|GMB|OEM|MAHLE|NGK|HI-Q|MK',re.I)
    vals=[re.sub(r'[^A-Z0-9]','',v.upper()) for h,v in o['x'] if not SKIP.search(h or '') and KEEP.search(h or '')]
    if not vals: vals=[re.sub(r'[^A-Z0-9]','',v.upper()) for h,v in o['x'] if not SKIP.search(h or '')]
    return ('MULTI' if F['brand'] in ('VALEO','KOYO','AISIN','METAL TEC','FIC','MAHLE') else F['make'])+'|'+F['brand']+'|'+'|'.join(sorted(v for v in vals if v))
groups={};G=[]
for o in out_rows:
    k=key(o); o['g']=groups.setdefault(k,len(groups))
# headers table to shrink
H={};HL=[]
def hid(h):
    if h not in H: H[h]=len(HL); HL.append(h)
    return H[h]
compact=[]
for o in out_rows:
    compact.append([o['f'],o['s'],[[hid(a),b] for a,b in o['x']],o.get('p',None),o.get('cp',None),o.get('na',0),o['g'],[[hid(a),b] for a,b in o.get('old',[])]])
for f in files: f.pop('rows',None)
json.dump(dict(files=files,headers=HL,rows=compact),open(out,'w'),separators=(',',':'),ensure_ascii=False)
print(len(files),len(out_rows),len(groups),sum(1 for f in files if not f['date']))
