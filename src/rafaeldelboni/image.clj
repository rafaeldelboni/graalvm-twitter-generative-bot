(ns rafaeldelboni.image
  (:require [rafaeldelboni.vertices :as vertices])
  (:import [com.sun.imageio.plugins.png PNGMetadata]
           [java.awt Color Graphics2D RenderingHints]
           [java.awt.geom GeneralPath]
           [java.awt.image BufferedImage]
           [java.io File]
           [java.nio.file Files]
           [java.util Base64]
           [javax.imageio IIOImage ImageIO ImageWriter]
           [javax.imageio.stream FileImageOutputStream]))

(set! *warn-on-reflection* true)

(def ^String generator-chunk-name "gnTr")

(defn ^:private get-png-imagewriter
  "Return an ImageWriter for PNG images"
  []
  (let [^java.util.Iterator iterator (ImageIO/getImageWritersBySuffix "png")]
    (when-not (.hasNext iterator)
      (throw (Exception. "No image writer for PNG")))
    (.next iterator)))

(defn ^:private make-generator-metadata
  "Create a PNGMetadata containing generator-string in its generator header chunk"
  [^String generator-string]
  (let [png-metadata (PNGMetadata.)]
    (.add (.unknownChunkType png-metadata) generator-chunk-name)
    (.add (.unknownChunkData png-metadata) (.getBytes generator-string))
    png-metadata))

(defn ^:private write-image
  "Write in the ImageWriter the image buffered bytes and its metadata"
  [^ImageWriter writer ^BufferedImage image ^PNGMetadata metadata]
  (let [^IIOImage iio-image (IIOImage. image nil nil)]
    (.setMetadata iio-image metadata)
    (.write writer nil iio-image nil)))

(defn ^:private file->base64 ^String [^File file]
  (->> file
       .toPath
       Files/readAllBytes
       (.encodeToString (Base64/getEncoder))))

(defn write-image-file!
  [^FileImageOutputStream output
   ^BufferedImage buffered-image]
  (let [^ImageWriter imagewriter (get-png-imagewriter)
        ^PNGMetadata metadata (make-generator-metadata "Hello Twitter!")]
    (.setOutput imagewriter output)
    (write-image imagewriter buffered-image metadata)
    (.flush output)
    (.close output)
    (.dispose imagewriter)))

(defn points->general-path ^GeneralPath [points]
  (let [general-path ^GeneralPath. (GeneralPath.)]
    (.moveTo general-path (-> points ffirst double) (-> points first last double))
    (doseq [point points
            :let [x (-> point first double)
                  y (-> point last double)]]
      (.lineTo general-path x y))
    (.closePath general-path)
    general-path))

(defn draw! ^Graphics2D [^BufferedImage buffered-image]
  (doto ^Graphics2D (.createGraphics buffered-image)
    (.setRenderingHint RenderingHints/KEY_RENDERING RenderingHints/VALUE_RENDER_QUALITY)
    (.setPaint (Color. 0 92 117))
    (.translate 250 250)
    (.fill (points->general-path (vertices/generate-polygon 1 [0 0] 250.0 0.0 0.0 6)))))

(defn generate! ^String
  ([]
   (generate! (File/createTempFile "generated-image" ".png")))
  ([^File file-output]
   (let [^FileImageOutputStream output (FileImageOutputStream. file-output)
         ^BufferedImage buffered-image (BufferedImage. 500 500 BufferedImage/TYPE_INT_ARGB)]
     (draw! buffered-image)
     (write-image-file! output buffered-image)
     (file->base64 file-output))))

;(generate! (File. "test.png"))
