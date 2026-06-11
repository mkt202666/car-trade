import os
import sys

# Force UTF-8 output
sys.stdout.reconfigure(encoding='utf-8')

files = [
    r'd:\ai_project\new-car-trade\output\prototype-analysis.txt',
    r'd:\ai_project\new-car-trade\output\routes-analysis.txt'
]

def fix_garbled_text(text):
    """Fix UTF-8 text that was misread as GBK.
    
    Algorithm: encode with GBK (ignore unencodable chars) -> decode as UTF-8.
    Handles special characters like \ufffd (replacement char) and \ufeff (BOM).
    """
    # Remove BOM if present
    if text.startswith('\ufeff'):
        text = text[1:]
    
    # Filter out characters that GBK can't encode (like replacement chars)
    gbk_compatible = []
    for ch in text:
        try:
            ch.encode('gbk')
            gbk_compatible.append(ch)
        except UnicodeEncodeError:
            # Skip unencodable characters
            pass
    
    cleaned = ''.join(gbk_compatible)
    
    # Now do the reverse fix: GBK encode -> UTF-8 decode
    try:
        gbk_bytes = cleaned.encode('gbk')
        fixed = gbk_bytes.decode('utf-8', errors='replace')
        return fixed
    except Exception as e:
        print(f'    Reverse fix failed: {e}')
        return text  # Return original if fix fails

for fpath in files:
    if not os.path.exists(fpath):
        print(f'NOT FOUND: {fpath}')
        continue
    
    # Read raw bytes
    with open(fpath, 'rb') as f:
        raw_bytes = f.read()
    
    # Strip UTF-8 BOM
    if raw_bytes.startswith(b'\xef\xbb\xbf'):
        raw_bytes = raw_bytes[3:]
        print(f'{fpath} - stripped UTF-8 BOM')
    
    # Decode as UTF-8 to get the garbled text
    garbled = raw_bytes.decode('utf-8', errors='replace')
    print(f'{fpath} - {len(garbled)} chars in file')
    
    # Try reverse fix
    fixed = fix_garbled_text(garbled)
    
    # Preview
    preview = fixed[:200].replace('\n', ' | ')
    print(f'  Preview: {preview[:180]}')
    
    # Check if fix worked (should contain CJK chars 4E00-9FFF)
    cjk_count = sum(1 for ch in fixed if '\u4e00' <= ch <= '\u9fff')
    print(f'  CJK chars in result: {cjk_count}')
    
    # Write back with UTF-8 BOM
    with open(fpath, 'w', encoding='utf-8-sig') as f:
        f.write(fixed)
    print(f'  Written with UTF-8 BOM')
    print()

print('DONE')
