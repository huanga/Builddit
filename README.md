Builddit
========

~~A hacky plugin to automatically generate schematic download link for the plot you are standing on.~~

After some testing, current plot systems does not enable me to do everything we want. As such, Builddit now
includes a basic plot system, as well as a plot generator, in order to facilitate the collaborative building
experience. More features and documentation will follow shortly.


Installation
============
1. Stop server
1. Drop plugin into plugins folder
1. Start server; this creates the plugin data folder with default configurations, but will not work yet
1. Stop server
1. Modify configuration files to include database login information
1. Create the database tables:

    ```sql
    CREATE TABLE `builddit_plot` (
     `id` int(10) NOT NULL AUTO_INCREMENT,
     `world` varchar(32) NOT NULL,
     `plotx` int(10) NOT NULL,
     `plotz` int(10) NOT NULL,
     `owner` varchar(24) NOT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `plot` (`world`,`plotx`,`plotz`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    CREATE TABLE `builddit_authorization` (
     `id` int(10) NOT NULL AUTO_INCREMENT,
     `pid` int(10) NOT NULL,
     `player` varchar(24) NOT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `pid-player` (`pid`,`player`)
     KEY `pid` (`pid`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    ```
1. Edit Bukkit.yml, and set the world's generator to "Builddit"
1. Delete or rename existing world
1. Start server
