About
-----

Tiny android wrapper app for [mpv-web](https://github.com/voidpp/mpv-web).

Scheme
------
```
+------------------------------------------+
|  +------+                                |
|  | mpv1 |----+        Computer 1         |
|  +------+    |                           |
|              |                           |
|  +------+    |    +-----------------+    |   +---------+
|  | mpv2 |----+----| MPV HTTP Router |----|---| MPV Web |
|  +------+    |    +-----------------+\   |   +---------+
|              |                        |  |
|  +------+    |                        |  |   +-------------+
|  | mpv3 |----+                        |  |   |  MPVRemote  |
|  +------+                             +--|---|   +---------+
+------------------------------------------+   |   | MPV Web |
                                               +---+---------+
```

* MPV HTTP Router: https://github.com/voidpp/mpv-http-router
* MPV Web: https://github.com/voidpp/mpv-web
* MPVRemote: https://github.com/voidpp/MPVRemote

Sreenshot
---------
https://raw.githubusercontent.com/voidpp/MPVRemote/master/screenshot.jpg
