/* *********************************************** **
 @Author		Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 0- BATTERY 
    0a- Constructor and its attributes
    0b- Method toString
    0c- Methods to create a list: createColumnsList, createListsInsideColumns
    0d- Methods for logic: calculateNumberOfFloorsPerColumn, setColumnValues, initializeBasementColumnFloors, initializeMultiColumnFloors, initializeUniqueColumnFloors
 1- COLUMN 
    1a- Constructor and its attributes
    1b- Method toString
    1c- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    1d- Methods for logic: findElevator, findNearestElevator, manageButtonStatusOn
    1e- Entry method: requestElevator
 2- ELEVATOR 
    2a- Constructor and its attributes
    2b- Method toString
    2c- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    2d- Methods for logic: moveElevator, moveUp, moveDown, manageButtonStatusOff, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, addFloorToFloorList, deleteFloorFromList
    2e- Entry method: requestFloor
 3- DOOR 
 4- BUTTON 
 5- DISPLAY 
 6- CONSTANTS (as enums)
 7- TESTING PROGRAM - SCENARIOS
 9- TESTING PROGRAM - CALL SCENARIOS

 CONTROLLED OBJECTS:
 Battery: contains a list of N columns
 Columns: controls a list of N elevators
 Elevators: controls doors, buttons, displays

 numberOfBasements
 numberOfFloors                                                     //Floors of the building excluding the number of basements
 totalNumberOfFloors = numberOfFloors + Math.abs(numberOfBasements) //Transform the number of basements to a positive number
 minBuildingFloor                                                   //Is equal to 1 OR equal the numberOfBasements if there is a basement
 maxBuildingFloor = numberOfFloors                                  //Is the last floor of the building
 maxWeight                                                          //Maximum weight an elevator can carry in KG

** ************************************************** */

package main

import (
	"fmt"
)


//------------------------------------------- BATTERY -----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
//----------------- Battery struct -----------------//
type Battery struct {
	id int
   numberOfColumns int
   totalNumberOfFloors int
   numberOfBasements int
   numberOfElevatorsPerColumn int
   status string
   columnsList []Column
   numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn();
   createColumnsList();
   setColumnValues();
   createListsInsideColumns();
}


//------------------------------------------- CONSTANTS (as enums) ----------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* BATTERY STATUS ******* */
type BatteryStatus string
const (
   baACTIVE BatteryStatus = "Active"
   baINACTIVE = "Inactive"
)

/* ******* COLUMN STATUS ******* */
type ColumnStatus string
const (
   coACTIVE ColumnStatus = "Active"
   coINACTIVE = "Inactive"
)

/* ******* ELEVATOR STATUS ******* */
type ElevatorStatus string
const (
   elIDLE ElevatorStatus = "Idle"
   elUP = "Up"
   elDOWN = "Down"
)

/* ******* BUTTONS STATUS ******* */
type ButtonStatus string
const (
   buON ButtonStatus = "On"
   buOFF = "Off"
)

/* ******* SENSORS STATUS ******* */
type SensorStatus string
const (
   seON SensorStatus = "On"
   seOFF = "Off"
)

/* ******* DOORS STATUS ******* */
type DoorStatus string
const (
   doOPENED DoorStatus = "Opened"
   doCLOSED = "Closed"
)

/* ******* DISPLAY STATUS ******* */
type DisplayStatus string
const (
   diON DisplayStatus = "On"
   diOFF = "Off"
)

/* ******* REQUESTED DIRECTION ******* */
type Direction string
const (
   diUP Direction = "Up"
   diDOWN = "Down"
)




// func soma(x int, y int) int { //last int is the return type, for more than 1 return use: func soma(x int) int (int, int) {}
// 	return x + y
// }

func main() {
	// var nome string = "Cindy"
	// fmt.Println("Hello, " + nome)
	// fmt.Println(soma(3, 4))
}
