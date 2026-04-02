import urllib.request, urllib.parse, re
from html import unescape
def search(query):
    print("Searching for: " + query)
    url = 'https://html.duckduckgo.com/html/?q=' + urllib.parse.quote(query)
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
    try:
        html = urllib.request.urlopen(req).read().decode('utf-8')
        results = re.findall(r'<a class="result__snippet[^>]*>(.*?)</a>', html, re.IGNORECASE | re.DOTALL)
        for i, res in enumerate(results[:5]):
            text = re.sub(r'<[^>]+>', '', res)
            print(str(i+1) + '. ' + unescape(text).strip())
    except Exception as e:
        print(e)

search('Call.STATE_AUDIO_PROCESSING')
