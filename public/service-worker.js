// AI TEACH — Progressive Web App Service Worker (PWA)
// ऑफ़लाइन कैशिंग और स्टैंडअलोन डेस्कटॉप/मोबाइल अनुभव के लिए

const CACHE_NAME = 'ai-teach-v1.0.0';
const STATIC_ASSETS = [
  '/',
  '/index.html',
  '/public/manifest.json',
  '/public/logo.png',
  '/public/icon-192.png',
  '/public/icon-512.png'
];

// 1. सर्विस वर्कर इंस्टॉल (Install Event) — महत्वपूर्ण एसेट्स प्री-कैश करें
self.addEventListener('install', (event) => {
  console.log('[Service Worker] Installing AI TEACH PWA Service Worker...');
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[Service Worker] Pre-caching static assets for offline use');
      return cache.addAll(STATIC_ASSETS).catch((err) => {
        console.warn('[Service Worker] Non-fatal caching warning:', err);
      });
    }).then(() => self.skipWaiting())
  );
});

// 2. सक्रियण (Activate Event) — पुराने कैशे को साफ़ करें
self.addEventListener('activate', (event) => {
  console.log('[Service Worker] Activating new Service Worker version...');
  event.waitUntil(
    caches.keys().then((keyList) => {
      return Promise.all(
        keyList.map((key) => {
          if (key !== CACHE_NAME) {
            console.log('[Service Worker] Deleting outdated cache:', key);
            return caches.delete(key);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// 3. फेच रणनीति (Fetch Event): नेटवर्क-फर्स्ट रणनीति के साथ ऑफ़लाइन फॉलबैक
self.addEventListener('fetch', (event) => {
  const requestUrl = new URL(event.request.url);

  // केवल GET अनुरोधों को कैश करें
  if (event.request.method !== 'GET') {
    return;
  }

  // इमेज और स्टैटिक एसेट्स के लिए कैश-फर्स्ट (Cache-first for static icons/logo)
  if (
    requestUrl.pathname.endsWith('.png') ||
    requestUrl.pathname.endsWith('.jpg') ||
    requestUrl.pathname.endsWith('.svg') ||
    requestUrl.pathname.endsWith('.webp')
  ) {
    event.respondWith(
      caches.match(event.request).then((cachedResponse) => {
        if (cachedResponse) {
          return cachedResponse;
        }
        return fetch(event.request).then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const responseToCache = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseToCache);
            });
          }
          return networkResponse;
        }).catch(() => {
          // ऑफ़लाइन होने पर डिफ़ॉल्ट लोगो दें
          return caches.match('/public/logo.png');
        });
      })
    );
    return;
  }

  // अन्य सभी पेजों और दस्तावेज़ों के लिए नेटवर्क-फर्स्ट रणनीति
  event.respondWith(
    fetch(event.request)
      .then((networkResponse) => {
        if (networkResponse && networkResponse.status === 200) {
          const responseToCache = networkResponse.clone();
          caches.open(CACHE_NAME).then((cache) => {
            cache.put(event.request, responseToCache);
          });
        }
        return networkResponse;
      })
      .catch(async () => {
        console.log('[Service Worker] Network failed, serving cached offline copy:', event.request.url);
        const cachedResponse = await caches.match(event.request);
        if (cachedResponse) {
          return cachedResponse;
        }

        // यदि HTML अनुरोध है और कैश में नहीं है, तो रूट पेज दें
        if (event.request.headers.get('accept')?.includes('text/html')) {
          return caches.match('/index.html') || caches.match('/');
        }

        return new Response('Offline: Content currently unavailable', {
          status: 503,
          statusText: 'Service Unavailable',
          headers: new Headers({ 'Content-Type': 'text/plain; charset=utf-8' })
        });
      })
  );
});
