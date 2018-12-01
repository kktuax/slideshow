# slideshow

Photo slideshow for Raspberry pi using JavaFrameBuffer

Place jar next to libframebuffer-0.1.0-SNAPSHOT.so, and start a slideshow:

```
java -Djava.awt.headless=true -Djava.library.path=$(pwd) -jar slideshow-0.0.1-SNAPSHOT.jar --folder="/path/to/my/photos"
```