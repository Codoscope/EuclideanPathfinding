# Euclidean Pathfinding

### Build Status

![Java CI](https://github.com/Codoscope/EuclideanPathfinding/workflows/Java%20CI/badge.svg)

### Overview

This program computes optimal paths between two points. Any direction movement is allowed but paths must circumvent obstacles.

It differs from (but uses) Dijkstra's algorithm which enables only horizontal and vertical movements (image on the left), by allowing to go straight from one point to a distant other one (image on the right). It uses the Euclidean distance to determine which paths are best.

![Dijstra path](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Dijkstra.png)
![Euclidean path](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/EuclideanPath.png)

### Running

The application is written in Java using Maven. To build it, install Java and Maven, `cd` into the repository, and run:

```
mvn package
cd target
java -jar euclideanPathfinding-1.0-SNAPSHOT.jar
```

### Principle

#### Algorithm

A matrix of the size of the image is created, where each cell will contain Manhattan distances from the departure (only including horizontal and vertical moves). Cells are filled step by step with distances using the following rule:

![Distance calculation rule](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Dijkstra_distance_calculation_rule.png)

When the arrival is reached and marked with a distance <img src="https://latex.codecogs.com/svg.latex?l" title="l" />, the algorithm still continues to fill cells having distances smaller than or equal to <img src="https://latex.codecogs.com/svg.latex?l" title="l" />, and then stops.

![Distance calculation rule](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Simple_area.png)

This defines an area that contains all possible Dijkstra's solutions. However, the Euclidean best path might not reside in it, as in this example :

![Distance calculation rule](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Simple_area_issue.png)

We need to enlarge the area enough to make sure that it contains the best Euclidean path. As proved in the next section, using <img src="https://latex.codecogs.com/svg.latex?\sqrt{2}l" title="\sqrt{2} l" /> as the new limit suffices. The algorithm contains also an optimization that shortens this enlarged area by additionally taking into account cells' positions.

![Distance calculation rule](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Improved_area.png)

Then, it skeletonizes it, creates a graph of possible paths, and smoothes each one. The shortest income is the best Euclidean path.

![Distance calculation rule](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Smoothed_path.png)

#### Enlarged area proof

For <img src="https://latex.codecogs.com/svg.latex?K" title="K" /> a category of paths that circumvent obstacles in the same way, we will notate <img src="https://latex.codecogs.com/svg.latex?K^1" title="K1" /> the size of Dijstra's solutions, and <img src="https://latex.codecogs.com/svg.latex?K^2" title="K2" /> the size of the best Euclidean solutions.

Let's define <img src="https://latex.codecogs.com/svg.latex?A^1" title="A1" /> the Dijktra's solution, and <img src="https://latex.codecogs.com/svg.latex?B^1" title="B1" /> another Manhattan solution. Let's assume that ![B^2\leqslant{}A^2](https://latex.codecogs.com/svg.latex?B^2\leqslant{}A^2). We want to make sure that  <img src="https://latex.codecogs.com/svg.latex?B^2" title="B2" /> is contained in the area.

![Distance calculation rule](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/Area_demonstration.png)

For any category <img src="https://latex.codecogs.com/svg.latex?K" title="K" />, we have <img src="https://latex.codecogs.com/svg.latex?K^2\leqslant{}K^1\leqslant\sqrt{2}K^2" title="K^2 \leqslant K^1 \leqslant \sqrt{2} K^2" />.

Thus:

* <img src="https://latex.codecogs.com/svg.latex?A^2\leqslant{}A^1\leqslant\sqrt{2}A^2" title="A^2 \leqslant A^1 \leqslant \sqrt{2} A^2" />
* <img src="https://latex.codecogs.com/svg.latex?B^2\leqslant{}B^1\leqslant\sqrt{2}B^2" title="B^2 \leqslant B^1 \leqslant \sqrt{2} B^2" />

Since ![B^2\leqslant{}A^2](https://latex.codecogs.com/svg.latex?B^2\leqslant{}A^2), we deduce that <img src="https://latex.codecogs.com/svg.latex?B^1\leqslant{}\sqrt{2}B^2\leqslant\sqrt{2}A^2\leqslant\sqrt{2}A^1" title="B^1 \leqslant \sqrt{2} B^2 \leqslant \sqrt{2} A^2 \leqslant \sqrt{2} A^1" />.

It comes that <img src="https://latex.codecogs.com/svg.latex?B^1\leqslant\sqrt{2}A^1" title="B^1 \leqslant \sqrt{2} A^1" />.

So, using ![]() <img src="https://latex.codecogs.com/svg.latex?\sqrt{2}l" title="\sqrt{2} l" /> as the limit for the area ensures that it will contain the Euclidean solution we are looking for.

### Usage

![Menu bar](https://github.com/Codoscope/EuclideanPathfinding/blob/master/resources/buttons.png)

* **Open a map**: Load an image that will serve as a map. Non-white pixels are considered to be obstacles. This is not necessary because a default map is created with no obstacles. You can try images under resources/examples.
* **< & >**: Select the previous or next category of paths when "Task to perform" is positionned to "Graph with paths", "Graph with smoothed paths" or "Smoothed paths".
* **Select the departure** & **Select the arrival**: Place the departure or the arrival. Press a button, and then click somewhere on the map area to choose their positions.
* **Switch obstacles**: Create or delete obstacles. Press the button and then click at different places on the map area.
* **Task to perform**: Choose the algorithm to apply.
* **Display mode**: Show or hide the grid. Please zoom in first for the grid to be legible.
* **+ & -**: Zoom in or zoom out.
* **Color iso-lines**: Colour area cells depending on their distances.


### TODOs

- Translate code to English
- Replace DoubleMaillon with LinkedList
- Fix ability for segments to cross diagonal blocks
- Harmonize indentation
- Add braces for all code blocks
- Clean code and split large functions

### License

This project is protected under the terms of [GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html).

Please contact me at codoscopus@gmail.com for further questions.

