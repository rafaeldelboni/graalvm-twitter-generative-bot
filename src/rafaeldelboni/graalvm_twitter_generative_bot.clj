(ns rafaeldelboni.graalvm-twitter-generative-bot
  (:require [rafaeldelboni.image :as image]
            [rafaeldelboni.oauth-v1 :as oauth]
            [rafaeldelboni.twitter :as twitter])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn envs->secrets []
  {:api-key (or (System/getenv "TWITTER_API_KEY")
                "your-twitter-api-key-here")
   :api-key-secret (or (System/getenv "TWITTER_API_KEY_SECRET")
                       "your-twitter-api-secret-key-here")
   :access-token (or (System/getenv "TWITTER_ACCESS_TOKEN")
                     "your-twitter-access-token-here")
   :access-token-secret (or (System/getenv "TWITTER_ACCESS_TOKEN_SECRET")
                            "your-twitter-access-token-secret-here")})

(defn -main
  [& _args]
  (try
    (let [generated-image (image/generate!)
          uploaded-media (twitter/image-upload generated-image
                                               (envs->secrets)
                                               {:nonce (oauth/nonce)
                                                :timestamp (oauth/timestamp)})
          media-id (get-in uploaded-media [:body :media_id])]
      (twitter/tweet "Clojure+GraalVM Simple Generated PNG"
                     media-id
                     (envs->secrets)
                     {:nonce (oauth/nonce)
                      :timestamp (oauth/timestamp)}))
    (catch Exception e
      (prn e))))
