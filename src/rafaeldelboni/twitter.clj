(ns rafaeldelboni.twitter
  (:require [rafaeldelboni.http-out :as http]
            [rafaeldelboni.oauth-v1 :as oauth]))

(defn tweet [tweet-msg secrets components]
  (let [request {:method :post
                 :url "https://api.twitter.com/1.1/statuses/update.json"
                 :query-params {:status tweet-msg}}
        header (oauth/payload->auth-header request secrets components)]
    (http/request! request header)))
