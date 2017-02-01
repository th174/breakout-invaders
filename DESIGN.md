CompSci 308: Game Analysis
===================

> This is the link to the assignment: [Game](http://www.cs.duke.edu/courses/compsci308/current/assign/01_game/)

Design Review
=======

###Status

For the most part, the code is both consistent and readable. That said, nobody other than me has read my code, so I don't know for sure. I feel like most of my code is fairly self-documenting. The dependencies for every class are pretty clear from the private instance variable declarations at the beginning of the class. The one exception is GameProperty, which is a static utility class. It's mainly there as a convenient way of referring to global game properties, such as screen bounds. 

The only part that might be confusing difficult to read in my opinion was the usage of Java Reflection in order to instantiate levels based on a read text file. Reflection violates OOP paradigms and depends heavily on class casts and try catch clauses. However, I believe it was still preferable to a massive if tree to decide what classes to instantiate.

```java
    String[] words = line.split("\\s+");
    if (words[0].contains("Enemy")) {
        try {
            Class<EnemyShip> enemyClass = (Class<EnemyShip>) Class.forName("game." + words[0]);
            Class[] constructorTypes = new Class[words.length - 1];
            Object[] constructorArgs = new Object[words.length - 1];
            if (enemyClass.equals(EnemyDoppleganger.class)) {
                myPlayers.forEach(p -> myEnemies.add(new EnemyDoppleganger((PaddleShip) p)));
            }
            for (int i = 0; i < constructorArgs.length; i++) {
                try {
                    constructorArgs[i] = Double.parseDouble(words[i + 1]);
                    constructorTypes[i] = double.class;
                } catch (NumberFormatException nfe) {
                    constructorArgs[i] = getClass().getDeclaredField(words[i + 1]).get(this);
                    constructorTypes[i] = getClass().getDeclaredField(words[i + 1]).getType();
                }
            }
            constructorArgs[0] = ((Double) constructorArgs[0]) * GameProperty.getWidth() * GameProperty.SCALE_X - (double) enemyClass.getDeclaredField("DEFAULT_SIZE").get(null) / 2;
            constructorArgs[1] = ((Double) constructorArgs[1]) * GameProperty.getHeight() * GameProperty.SCALE_Y;
            myEnemies.add(enemyClass.getDeclaredConstructor(constructorTypes).newInstance(constructorArgs));
        } catch (Exception e) {
            System.out.println("Instantiation failed for class: " + words[0] + "\n\n");
            e.printStackTrace();
        }
    }
```

I mentioned in my analysis about having going through a lot of trouble working out a way to accurately model the ball bouncing between off of another object at arbitrary locations and arbitrary angles. These two methods are primarily responsible for the logic handling bounces. Comments appear fairly infrequently in my project, but these two methods deserve better documentation because of the heavy math involved. The end result is much prettier and much cleaner than the first edition with 90 lines of nasty if statements.

```java
    public void bounce(double wallangle) {
        wallangle = (wallangle + 100 * Math.PI) % (Math.PI * 2);
        moveAngle = (moveAngle + 100 * Math.PI) % (Math.PI * 2);
        //only bounce if diff in angles > 60 degrees
        if (angleDistance(wallangle + Math.PI / 2, moveAngle) >= Math.PI / 2) {
            moveAngle = ((wallangle + Math.PI / 2) * 2 - moveAngle + 101 * Math.PI) % (Math.PI * 2);
            //prevent ball getting stuck horizontally by slightly fudging its bounce angle away from perfectly horizontal
            //if tree too long?
            if (moveAngle > 0 && moveAngle < Math.PI / 36) {
                moveAngle = Math.PI / 36;
            } else if (moveAngle > Math.PI * 35 / 36 && moveAngle < Math.PI) {
                moveAngle = Math.PI * 35 / 36;
            } else if (moveAngle >= Math.PI && moveAngle < Math.PI * 37 / 36) {
                moveAngle = Math.PI * 37 / 36;
            } else if (moveAngle > Math.PI * 71 / 36 && moveAngle <= Math.PI * 2) {
                moveAngle = Math.PI * 71 / 36;
            }
            if (parentShip instanceof EnemyShip) {
                new MediaPlayer(bounceSFXEnemy).play();
            } else {
                new MediaPlayer(bounceSFXAlly).play();
            }
        }
    }
    
    //calculate bounce angle on non-flat surface (calculate derivative of ellipse at midpoint of intersection)
    public void bounce(Ellipse shipHitBox, Shape intersection) {
        double contactPointXLocal = shipHitBox.getCenterX() - (intersection.getBoundsInParent().getMaxX() + intersection.getBoundsInParent().getMaxX()) / 2;
        double contactPointYLocal = shipHitBox.getCenterY() - (intersection.getBoundsInParent().getMaxY() + intersection.getBoundsInParent().getMaxY()) / 2;
        bounce(Math.atan2(Math.pow(shipHitBox.getRadiusY(), 2) * contactPointXLocal, Math.pow(shipHitBox.getRadiusX(), 2) * contactPointYLocal));
    }
```

###Design

####Overarching Design
>The vast majority of classes in the program are organized in a tree structure. Almost all the classes inherit the Interface GameObj, which represents the basic framework for any kind of object drawn to the screen. At the second level of the tree are Ship and Projectile. Every Ship interacts with a collection of other Ships, and every Ship has a collection of Projectiles. Ships are then divided into PlayerShips and EnemyShips, where PlayerShips have methods handling user input, while EnemyShips have methods that deal with some sort of autonomous behavior. Projectiles can include Powerups, which interact in unique ways with PlayerShips only. This tree structure makes it really easy to extend functionality by adding child nodes at any point in the tree. Many of the abstract classes have default implementations in their methods, most of which can be overriden with original behavior or left alone if necessary.
>Classes that don't fit into hierarchy are: 
> * GameProperty, which is a static utility class
> * Main, which is responsible for actually drawing objects to the screen, and for handling program flow

####Adding a level
> Adding a new level to the game would be as simple as writing another level#.lvl, adding it to the classpath, changing the value of game.Main.NUM_LEVELS, and adding the file to the array of level filenames. Of course, you should probably extend EnemyShip and Projectile to create new, unique enemies and obstacles to overcome in your new level, although that's not absolutely necessary.

####Features
> Powerups are implemented by the Powerup abstract class. I decided to have Powerups extend the Projectile class instead, of creating a separate top level abstract class. This meant that Powerups would inherit some of the functionality of weapon projectiles, allowing its methods to be called through polymorphism. In addition, to the methods of Projectile, Powerups also inherited the collision checking logic and position updating logic applied to the other projectiles. However, this design also meant that Powerups inherited some irrelevant, unused methods and instance variables, such as damage. Powerup effects required a new method unique to Powerups in order to implement.

> The rounded paddle is implemented as 4 separate hitboxes, 2 Rectangles and 2 Ellipses. All four are checked for intersection. All of this is done within the method PaddleShip::checkBallBounce, and the hitboxes are stored in a private array instance. The implementation details are hidden within a private method and a private variable. From the updateProjectiles method inherited from Ship, all that can be seen is a single call to PaddleShip::checkBallBounce.

####Alternative Designs
* I decided to implement the majority of the functional code in this project in abstract superclasses. This way, I repeated code, and made it very easy to add new classes to this program. In many cases, the superclasses had a limited or default implementation, so any new subclasses would only be responsible for overriding the superclass new unique behavior seen nowhere else. However, this design decision meant that I frequently had built-in inherited functionality that was just not taken advantage of. Each subclass would inherit methods and instance variables that were never used. For example, EnemyShip has a default move method that moves left and right randomly, while slowly progressing down the screen. However, only the Soldier, Spectre, Wraith, and Sentry actually use this behavior. Gunner modifies this behavior by only changing directions when hitting a screen edge, while Doppleganger overrides EnemyShip::move completely with unique behavior. A few EnemyShips don't move at all, and were just instantiated with movespeed 0 in 1 or both dimensions. Another example is that all EnemyShips respond to a Collection of multiple PlayerShips, despite there only being one player in this game total. In many parts of the game, the PlayerShip is only referred to in the context of a collection, despite the collection only having one element. This means that those game elements fully support multiple players in cooperative gameplay. However, multiplayer gameplay is never taken advantage of, despite its functionality being built into many classes. Thus, the additional complexity could be seen as wasted. At the same time, the result is that the game is easily extendible to incorporate this new functionality.

* An alternative design philosophy is to only code for what you prepare to use. There is an idea that each object should possess the bare minimum code for that particular object to function, and no more. This minimalist design philosophy keeps the top level design abstract and simple. In this design philosophy, I would have left most of my implementation to my concrete classes, while abstract classes would be mainly devoid of functional code and instance variables. This type of application design tends to be very clean, modular, light, and efficient, but tends to be more difficult to avoid repeated code and more difficult to extend new functionality.  I ultimately decided against this type of design philosophy, because I wanted to prioritize the extendability of my program. By doing as much work in higher level classes as I can, it minimizes the amount of work needed to add new functionality. 