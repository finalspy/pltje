# pltje

Exemple simple de bot de vote exploitant le fait qu'une session PHP peut voter de multiples fois.
Permet d'augmenter les XHR et d'éviter de DOS le serveur (plus performant que 1 vote / session).
Ce bot permet de faire entre 5 et 20 votes / secondes au delà le serveur répond mal... 

Pour les puristes, aucun intérêt a utiliser JSOUP etc. je sais, juste j'avais le code sous la main donc en bonne feignasse CC :P 
Et non pas besoin de vertx etc. dans le pom (juste j'ai oublié d'effacer suite a des tests).
