# Squirrel
Squirrel is an android app created during Green Hack 2018 at Cambridge. The purpose of the application is to tell stories about trees and encourage people to plane new trees.

## How does it work?

- You can plant a tree in real life, and then tap plant tree in the app.
- A screen will appear where one can name the tree, give it a story, and choose from basic types.

![](https://i.imgur.com/Dr2TZdp.png =250x) ![](https://i.imgur.com/cGEcLoV.png =250x)

- After planting the tree, it will appear publicly in a map where everyone can see information about the trees nearby.

![](https://i.imgur.com/HIcGgFA.png =250x)


## How did we build it?

**Android**
The app itself is built to run on android devices, supporting Android N and newer versions. The device deals with locating the user on the map and displaying all the information about trees.

**Kotlin**
We used Kotlin as the development lenguage. We choose Kotlin due to its conciseness and speed of development.

**Flask**
We used Flask for Python to create our backend. It was a rest API that dealt with storing the information about the trees worldwide and sending it to phones that were requesting the data.

## Challenges we ran into
- Inventing a method of verifing whether people planted real trees is not straightforward

## Area for improvement
- We plan to create an augmented reality camera inside the app that would make it possible to look around and see the stories appear next to trees.
- Add stores with trees or seeds to make it even more encouraging and easy to plant trees
- Add a manual on how to plant trees
- Make the app available for IOS as well
