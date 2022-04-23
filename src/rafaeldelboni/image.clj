(ns rafaeldelboni.image
  (:import [com.sun.imageio.plugins.png PNGMetadata]
           [java.awt Graphics2D]
           [java.awt.image BufferedImage]
           [java.io File]
           [javax.imageio ImageIO IIOImage ImageWriter]
           [javax.imageio.stream FileImageOutputStream]))

(set! *warn-on-reflection* true)

(def ^String generator-chunk-name "gnTr")

(defn get-png-imagewriter
  "Return an ImageWriter for PNG images"
  []
  (let [^java.util.Iterator iterator (ImageIO/getImageWritersBySuffix "png")]
    (when-not (.hasNext iterator)
      (throw (Exception. "No image writer for PNG")))
    (.next iterator)))

(defn make-generator-metadata
  "Create a PNGMetadata containing generator-string in its generator header chunk"
  [^String generator-string]
  (let [png-metadata (PNGMetadata.)]
    (.add (.unknownChunkType png-metadata) generator-chunk-name)
    (.add (.unknownChunkData png-metadata) (.getBytes generator-string))
    png-metadata))

(defn write-image
  "Write in the ImageWriter the image buffered bytes and its metadata"
  [^ImageWriter writer ^BufferedImage image ^PNGMetadata metadata]
  (let [^IIOImage iio-image (IIOImage. image nil nil)]
    (.setMetadata iio-image metadata)
    (.write writer nil iio-image nil)))

(defn generate!
  []
  (let [^File file-output (File/createTempFile "generated-image" ".png")
        ^FileImageOutputStream output (FileImageOutputStream. file-output)
        ^ImageWriter imagewriter (get-png-imagewriter)
        ^BufferedImage bi (BufferedImage. 16 16 BufferedImage/TYPE_INT_ARGB)
        ^Graphics2D g (.createGraphics bi)
        ^PNGMetadata metadata (make-generator-metadata "Hello Twitter!")]
    (.drawLine g 0 0 10 10)
    (.drawLine g 0 15 15 0)
    (.setOutput imagewriter output)
    (write-image imagewriter bi metadata)
    (.flush output)
    (.close output)
    (.dispose imagewriter)
    file-output))
