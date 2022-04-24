(ns rafaeldelboni.twitter
  (:require [rafaeldelboni.http-out :as http]
            [rafaeldelboni.oauth-v1 :as oauth]))

(set! *warn-on-reflection* true)

(defn tweet [tweet-msg image-id secrets components]
  (let [request {:method :post
                 :url "https://api.twitter.com/1.1/statuses/update.json"
                 :query-params {:status tweet-msg
                                :media_ids image-id}}
        header (oauth/payload->auth-header request secrets components)]
    (http/request! request header)))

(defn image-upload [image secrets components]
  (let [request {:method :post
                 :url "https://upload.twitter.com/1.1/media/upload.json"
                 :query-params {:media_category "TWEET_IMAGE"
                                :media_data image}}
        header (oauth/payload->auth-header request secrets components)]
    (http/request! request header)))
