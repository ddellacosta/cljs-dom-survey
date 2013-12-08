(ns cljs-dom-survey.core
  (:import [goog.dom query]) ; super handy, not available by default in goog.dom
  (:require
   [clojure.string :refer [capitalize]]
   [goog.dom :as gdom]
   [goog.events :as gevents]
   [domina :as dom]
   [domina.css :as css]
   [domina.events :as events]
   [dommy.core :as dommy]
   [jayq.core :as jayq :refer [$]]
   [enfocus.core :as ef]
   [enfocus.events :as ef-events])
  (:require-macros [enfocus.macros :as em])
  (:use-macros
   [dommy.macros :only [node sel sel1]]))

(defn log [lstr] (.log js/console lstr))

;; "Raw" DOM Manipulation
(defn add-annoying-alert-listener_rawjs!
  [a]
  (.addEventListener ; you have to support IE8 and lower? Bummer.
   a "click"
   (fn [evt] 
     (let [atxt (-> evt (.-currentTarget) (.-innerHTML))
           msg  (str "You clicked " atxt)]
       (.alert js/window msg)
       (.preventDefault evt)))))

(defn add-menu-link_rawjs!
  [link]
  ;; XPath evaluate is not supported on IE...
  (let [uls      (.evaluate js/document
                            "//div[@id='menu']/ul"
                            js/document nil "XPathResult.ANY_TYPE" nil)
        ul       (.iterateNext uls)
        new-li   (.createElement js/document "li")
        a        (.createElement js/document "a")]
    (set! (.-href a) (last link))
    (set! (.-innerHTML a)  (-> link first name capitalize))
    (.appendChild new-li a)
    (.appendChild ul new-li)
    (add-annoying-alert-listener_rawjs! a)))

;; Google Closure

(defn add-annoying-alert-listener_goog!
  [a]
  (gevents/listen
   a goog.events.EventType.CLICK
   (fn [evt]
     (let [atxt (-> evt .-currentTarget gdom/getTextContent)
           msg  (str "You clicked " atxt)]
       (.alert js/window msg)
       (.preventDefault evt)))))

(defn add-menu-link_goog!
  [link]
  (let [ul (first (query "#menu ul"))
        li (gdom/createElement "li")
        a  (gdom/createElement "a")]
    (set! (.-href a) (last link))
    (gdom/setTextContent a (-> link first name capitalize))
    (gdom/appendChild li a)
    (gdom/appendChild ul li)
    (add-annoying-alert-listener_goog! a)))

;; domina

(defn add-annoying-alert-listener_domina!
  [a]
  (events/listen!
   (css/sel "a") :click
   (fn [evt]
     (let [atxt (-> evt events/current-target dom/text)
           msg  (str "You clicked " atxt)]
       (.alert js/window msg)
       (events/prevent-default evt)))))

(defn add-menu-link_domina!
  [link]
  (let [ul (css/sel "#menu ul")
        li (dom/html-to-dom "<li></li>")
        a  (dom/html-to-dom "<a></a>")]
    (doto a
      (dom/set-text! (-> link first name capitalize))
      (dom/set-attr! :href (last link)))
    (dom/append! li a)
    (dom/append! ul li)
    (add-annoying-alert-listener_domina! a)))

;; Dommy

(defn add-annoying-alert-listener_dommy!
  [a]
  (dommy/listen!
   a :click
   (fn [evt]
     (let [atxt (-> evt (.-currentTarget) dommy/text)
           msg  (str "You clicked " atxt)]
       (.alert js/window msg)
       (.preventDefault evt)))))

(defn add-menu-link_dommy!
  [link]
  (let [ul       (sel1 [:#menu :ul])
        link-txt (-> link first name capitalize)
        li       (node [:li [:a {:href (last link)} link-txt]])]
    (dommy/append! ul li)
    (add-annoying-alert-listener_dommy! (sel1 li :a))))

;; jayq

(defn add-annoying-alert-listener_jayq!
  [a]
  (jayq/on a :click
      (fn [evt]
        (let [atxt (-> evt (.-currentTarget) dommy/text)
              msg  (str "You clicked " atxt)]
          (.alert js/window msg)
          (.preventDefault evt)))))

(defn add-menu-link_jayq!
  [link]
  (let [$ul      ($ "#menu ul")
        link-txt (-> link first name capitalize)
        li-str   (str "<li><a href=" (last link) ">" link-txt "</a></li>")]
    (jayq/append $ul li-str)
    (add-annoying-alert-listener_jayq! (jayq/find $ul :a))))

;; Enfocus

(em/defaction add-annoying-alert-listener_enfocus!
  [href]
  [(str "a[href=" href "]")]
  (ef-events/listen
   :click
   (fn [evt]
     (let [atxt (-> evt (.-currentTarget) (.-text))
           msg  (str "You clicked " atxt)]
       (.alert js/window msg)
       (.preventDefault evt)))))

(defn add-menu-link_enfocus!
  [link]
  (let [link-str (-> link first name capitalize)
        href     (last link)
        li       (ef/html [:li [:a {:href href} link-str]])]
    (ef/at js/document ["#menu ul"] (ef/append li))
    (add-annoying-alert-listener_enfocus! href)))

(set! (.-onload js/window)
      #(add-menu-link_rawjs! [:link4 "#link4"]))
;;      #(add-menu-link_goog! [:link4 "#link4"]))
;;      #(add-menu-link_domina! [:link4 "#link4"]))
;;      #(add-menu-link_dommy! [:link4 "#link4"]))
;;      #(add-menu-link_jayq! [:link4 "#link4"]))
;;      #(add-menu-link_enfocus! [:link4 "#link4"]))
