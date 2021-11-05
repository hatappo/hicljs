(ns hicljs.core
  (:require
    ["express" :as express]))


;; (defn main
;;   [& args]
;;   (js/console.log "Hi there! こんにちは"))

;; このアトムには、サーバーインスタンスが格納されます。後で破棄できるようにするためです。
(defonce server (atom nil))


(defn routes
  "非常にわかりやすいですが、詳細については
   https://expressjs.com/en/guide/routing.html
   にある express のドキュメントを参照してください。"
  [^js app]
  (.get app "/" (fn [_req res] (.send res "Hi ほげほげ"))))


(defn start-server
  "ルートだけでなくセッションの使用も設定して
   ENV で指定されたポートでサーバーを起動します。
   見つからない場合は3000番ポートで起動します。"
  []
  (let [app (express)
        prod? (= (.get app "env") "production")
        port (if (nil? (.-PORT (.-env js/process)))
               3000
               (int (.-PORT (.-env js/process))))]
    (when prod? (.set app "trust proxy" 1))
    (.use app "/assets" (.static express "assets"))
    (routes app)
    (.listen app port (fn [] (prn "Listening ...")))))


(defn start!
  "サーバーを起動するとともに
   `server` アトムをサーバーインスタンスで更新します。
   後でサーバーを停止できるように。"
  []
  (reset! server (start-server)))


(defn stop!
  "`server` の接続を閉じるとともに
   `server` のアトムを `nil` に設定します。"
  []
  (.close @server)
  (reset! server nil))


(defn main
  "Main entrypoint to the app."
  [& _args]
  (start!))
