# Stroke Rehabilitation App

## 1. Introduction
This is a simple mobile app aimed at helping stroke patients recover their motor skills, coordination, and a certain extent, cognitive capabilities, through repetitions of simple exercises. 

There are a prototype, designed using Balsamiq Wireframe, and 2 versions of this app (Android and iOS). Both versions use Firebase to store patients' data and progress.

### Scope
- Given the constrained range of motion and cognitive function often experienced by stroke patients, the app was designed with the presumption that their caregiver will navigate the interface. The patients' interaction is primarily focused on engaging with the exercises.
- The app was designed for a single user - one patient using the app exclusively. It does not incorporate login or user-switching features, and the database stores data for the individual user.
- Responsive design was not taken into account; both Android and iOS versions were designed for specific device dimensions.


## 2. Features
There are 2 distinct exercises in the app:
1. "Touch" exercise: the user is prompted to tap three buttons labelled 1, 2, and 3 in the correct **ascending sequence**. In each iteration, all three buttons are displayed simultaneously in random positions on the screen. Accurately tapping the correct button removes it from view. Successfully completion of tapping all three buttons sequentially counts as one "repetition", leading to the next "repetition". 
2. "Hold": the exercise is similar to the "Touch" exercise, with a variation: Instead of tapping, the user must tap and hold down the correct button for 3 seconds to register success and remove it from the screen.

- For each exercise, there are:
  -  2 **modes**:
      - Goal Mode: the user can set an objective to complete, choosing either a specific number of repetitions or a time limit for the exercise
      - Free-Play Mode: the user can engage in unlimited practice sessions

  - A variety of **exercise customisation options**, including:
    - Adjusting the number of buttons, from 3 up to 5
    - Opting for buttons to appear randomly or in a designated sequence
    - Activating a visual indication for the next button in the sequence
    - Modifying the size of the buttons.

Other features:
- View total time spent and repetitions completed for each exercise
- Access their history of attempts, either completed or uncompleted, including start and end times, repetition count, exercise mode, customisation options, and a detailed log of button tapped
- Easily remove any undesired attempts from their history
- Share their attempts to other applications (e.g. notes) by exporting data in CSV text format
- Edit their username

## 3. Some app screenshots
### Android
<img src="./Android/App%20screenshots/Exercise%20main.PNG" alt="App Front" width="300"/>
<img src="./Android/App%20screenshots/Touch options.PNG" alt="Touch Exercise Customisation" width="300"/>
<img src="./Android/App%20screenshots/Touch rep.PNG" alt="Touch Exercise Gameplay" width="300"/>
<img src="./Android/App%20screenshots/History all.PNG" alt="History Records" width="300"/>

### iOS
<img src="./iOS/App screenshots/Exercise main.png" alt="App Front" width="300"/>
<img src="./iOS/App screenshots/Touch options.png" alt="App Front" width="300"/>
<img src="./iOS/App screenshots/Touch rep.png" alt="App Front" width="300"/>
<img src="./iOS/App screenshots/History all.png" alt="Android App Front" width="300"/>
