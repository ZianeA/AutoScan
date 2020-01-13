# Codebar-Reader
A stocktake & barcode scanner app for inventory management. It facilitate the scanning of equipment and assets within a business or an organization. It's designed to work both offline and online.

# Functionality
The app starts by prompting the user to enter his or her username and password. This is so we can identify the person performing the scanning. 

![Login Screen Demo](https://i.imgur.com/xmnUIjSl.png)

Once the user is logged in, the app will start fetching all the equipment to create a local copy.

[![Download Screen Demo](https://i.imgur.com/WGWykga.png)](https://streamable.com/w4h4e)

*\*Notice how the app automatically resumes download after connexion lost, after the app goes into the background or after the app is killed.*

When download is complete, the user can start scanning. There are two main screens. The first screen contains a list of desks the user has already scanned ordered by scan date. 

![Desk Screen](https://i.imgur.com/kw6BWull.png)

By scanning a desk barcode, they will be taken to the second screen containing a list of equipment that belongs to that particular desk.

![Equipment Screen](https://i.imgur.com/gf6XXmxl.png)

In This screen, there are three colors to distinguish the scan state of each item in the list. The color red indicates that the item has not been scanned yet, yellow for scanned by not synced items, and finally green for items that have been successfully scanned and synced with the server. At the header, there are tags displaying useful statistics. The same tags can double as filters. The following screenshot has only scanned and synced tag selected:

![Tags](https://i.imgur.com/x7FGkhHl.png)

*\*Notice how only the scanned and synchronized items are visible.*

Scanned equipment condition can be changed by selecting from the dropdown menu.

![Equipment condition dropdown menu](https://i.imgur.com/Dw8sZKzl.png)

All the synchronization happens in the background not requiring any user intervention. Just after the synchronization is complete, a notification is sent to notify the user.

![Notification](https://i.imgur.com/p8U708nl.png)

The server is a custom Odoo ERP module. It uses XMLRPC for communication. The server address can be configured in the settings screen.

![Settings screen](https://i.imgur.com/KCiqJhgl.png)

See this [video](https://streamable.com/ld63h) for a complete showcase.

# Architecture
The project is built in Kotlin and follows single Activity, multiple Fragments architecture. It based on the MVP-Passive View pattern.

# Libraries
* Dagger 2 for dependency injection.
* Rxjava for a reactive UI and asychronous operations.
* FragNav for naviation.
* Epoxy as Recyclerview  adapter.
* Room as ORM.
