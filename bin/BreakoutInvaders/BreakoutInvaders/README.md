README
=================================
##**Gameplay**
* The player must defend against an invasion by piloting a ship equipped with an energy paddle and a Plasmaball. The Plasmaball will bounce off of all surfaces, including your paddle, except for the bottom edge of the screen. Your goal is to clear a field of enemies by bouncing the ball around using the energy paddle.
	* Your ship comes equipped with shield and hull defenses. Taking hits from the enemy fleet will deplete your shield integrity, and then your hull health once your shield is empty. Shields also slowly deplete on their own over time.
	* Contact between the energy paddle and the Plasmaball allows you to charge your shields and replenish your shield integrity by a small amount.
* Levels consist of waves of enemies which must be destroyed to clear the level. The invasion fleet contains ships with varying health, movement, and shooting patterns.
	* Level 1 features Gunners, which move quickly, predictable, while shooting volleys of fast, low damage lasers.
	* Level 2 features Wraiths and Spectres, which unpredictably move and shoot single high damage laser bolts.
		* Wraiths are cloaked and invisible, and must be hit by the ball to be destroyed.
		* Spectres are immune to damage and cannot be hit by the ball. Instead, they lose health gradually over time.
	* Level 3 features Missle Launchers and a Doppleganger.
		* Missile Launchers are stationary, but slow homing missiles that do high damage to anything within the blast radius at detonation.
		* Dopplegangers are a corrupted version of the player's own ship, mirroring the players own movements. They also fire bouncing balls that move similarly to the player's own ball.
* Victory lies in completing each level without allowing your ship to be destroyed.
###**Controls**
- Mousemove:
> Moves player1's ship

- Mouse1click:
> Releases the ball from the paddle

- Spacebar:
> Pause, Unpause, and Continue game

####Debug keys
- 'B':
> Spawn a new ball in the center of the paddle 

- 'H':
> Sets your health to 100% while toggled on

- 'M': 
> Makes the paddle "catch" the ball while toggled on, similar to the magnetic paddle powerup.

- 'N': 
>Instantly destroy all enemies in the current level. This ends the current level.

###Command Line Arguments
- `--dimensions=DIMENSION`
   > The game will use a user-defined resolution instead of a detected native resolution. NOTE: This game was designed primarily on a 1080p screen, and does not fully support other dimensions. Some UI elements may break, and gameplay experience may not be optimized.
- `--no-fit-screen=BOOLEAN`
   > If true, the game will use the default resolution (1920x1080) instead of your screen's detected native resolution. Use this option on very high resolution screens.
- `--hitboxes=BOOLEAN`
   > If true, the game will show debug hitboxes. This feature is only intended for debug use. Turning on hitboxes on a low end machine may cause low framerates and poor performance.
- `--fullscreen=BOOLEAN`
   > If false, the game will run in windowed mode. Because the game runs by default on your screen's native resolution, you should use this option in conjunction with `--no-fit-screen` on very high resolution screens.

###**Power-ups**
Power-ups drop randomly from all enemies when they are destroyed, using [Pseudo-Random Distribution](http://wiki.teamliquid.net/dota2/Pseudo_Random_Distribution). 
Averaged over a long period of time, each enemy ship has a total 19.8% chance to drop a Power-up.

Making contact with powerups using with your ship will trigger their effects. 

####Types
- Extra Ball (~7.3% drop rate):
> Creates an extra ball on your paddle. It can be released by clicking the mouse, or will release automatically after a few seconds. 

- Magnetic Paddle (~6.6% drop rate): 
> For the next 20 seconds, contact with the ball and the paddle will pull the ball to the center of the paddle instead of bouncing it back. Clicking the mouse releases the ball.
 
- Health Pack (~5.9% drop rate): 
> Permanently regenerates 20% of your maximum hp, up to 100%

###Source Files
All of the following files are needed to run the program
**Java Classes**
* game.Main (main)
* game.GameProperty
* game.EnemyScout
* game.EnemyWraith
* game.PlasmaBall
* game.Powerup
* game.EnemySentry   
* game.GameObj
* game.PlayerShip
* game.Projectile.
* game.EnemyDoppleganger
* game.EnemyShip
* game.Lase
* game.Missile
* game.PowerupBallMagnet
* game.EnemyGunner
* game.EnemySoldier
* game.PaddleShip
* game.PowerupExtraBall.
* game.EnemyMortar
* game.EnemySpectre
* game.PowerupHealthPack
* game.Ship
**Levels**
* game/level1.lvl
* game/level2.lvl
* game/level3.lvl
**Resources**
* game/resources/Background.png
* game/resources/ballmagnet.png
* game/resources/BGM1.mp3
* game/resources/BGM2.mp3
* game/resources/BGM3.wav
* game/resources/bounce2.wav
* game/resources/bounce.wav
* game/resources/doppleganger2.png
* game/resources/doppleganger.png
* game/resources/explosion1.png
* game/resources/explosion2.png
* game/resources/explosion3.png
* game/resources/explosion4.png
* game/resources/explosion5.png
* game/resources/explosion-old1.wav
* game/resources/explosion.wav
* game/resources/extraball.png
* game/resources/gunner2.png
* game/resources/gunner.png
* game/resources/healthpack.png
* game/resources/log.txt
* game/resources/missile.png
* game/resources/mortar2.png
* game/resources/mortar.png
* game/resources/MxL3hcPh.jpg
* game/resources/paddleship.png
* game/resources/plasmaball.png
* game/resources/scout2.png
* game/resources/scout.png
* game/resources/sentry2.png
* game/resources/sentry.png
* game/resources/shadowball.png
* game/resources/soldier2.png
* game/resources/soldier.png
* game/resources/spectre2.png
* game/resources/spectre.png
* game/resources/wraith.png



Credits
-----------------------------------------
**Coding**
All code was Java code was written, designed, and built by Tianyue Huang (th174).  
Artwork was derived from images found online, edited in Adobe Photoshop.
Credit to StackOverflow for explaining to me how Java Reflection worked.
Time taken: ~35 hours

**Credits for original artwork go to**
[pzUH](http://www.gameart2d.com)
[MillionthVector](http://millionthvector.blogspot.com/)

**Credits for the BGM go to**
[ParagonX9](paragonx9.newgrounds.com)
[zeroscar](zeroscar.bandcamp.com)