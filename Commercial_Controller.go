/* *********************************************** **
 @Author		Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 0- BATTERY
    0a- Function to create Battery
    0b- Function toString
    0c- Functions to create a list: createColumnsList, createListsInsideColumns
    0d- Functions for logic: calculateNumberOfFloorsPerColumn, setColumnValues, initializeBasementColumnFloors, initializeMultiColumnFloors, initializeUniqueColumnFloors
 1- COLUMN
    1a- Function to create Column
    1b- Function toString
    1c- Functions to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    1d- Functions for logic: findElevator, findNearestElevator, manageButtonStatusOn
    1e- Entry Function: requestElevator
 2- ELEVATOR
    2a- Function to create Elevator
    2b- Function toString
    2c- Functions to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    2d- Functions for logic: moveElevator, moveUp, moveDown, manageButtonStatusOff, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, addFloorToFloorList, deleteFloorFromList
    2e- Entry Function: requestFloor
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

import "fmt"

//------------------------------------------- BATTERY -----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Battery struct {
	id                         int
	numberOfColumns            int
	minBuildingFloor           int //Is equal to 1 OR equal the numberOfBasements if there is a basement
	maxBuildingFloor           int //Is the last floor of the building
	numberOfFloors             int //Floors of the building excluding the number of basements
	numberOfBasements          int
	totalNumberOfFloors        int //numberOfFloors + Math.abs(numberOfBasements)
	numberOfElevatorsPerColumn int
	numberOfFloorsPerColumn    int
	status                     BatteryStatus
	columnsList                []Column
}

//----------------- Function to create Battery -----------------//
func newBattery(id int, numberOfColumns int, totalNumberOfFloors int, numberOfBasements int, numberOfElevatorsPerColumn int, batteryStatus BatteryStatus) *Battery {
	b := new(Battery)
	b.id = id
	b.numberOfColumns = numberOfColumns
	b.totalNumberOfFloors = totalNumberOfFloors
	b.numberOfBasements = numberOfBasements
	b.numberOfElevatorsPerColumn = numberOfElevatorsPerColumn
	b.status = batteryStatus
	b.columnsList = []Column{}
	// b.numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn()
	createColumnsList(b)
	// setColumnValues(b)
	createListsInsideColumns(b)

	return b
}

//----------------- Functions to create a list -----------------//
/* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
func createColumnsList(b *Battery) {
	name := 'A'
	for i := 1; i <= b.numberOfColumns; i++ {
		c := newColumn(i, name, columnActive, b.numberOfElevatorsPerColumn, b.numberOfFloorsPerColumn, b.numberOfBasements, b)
		b.columnsList = append(b.columnsList, *c)
		fmt.Println("Created column" + string(c.name))
		name++
	}
}

/* ******* CALL FUNCTIONS TO CREATE THE LISTS INSIDE EACH COLUMN ******* */
func createListsInsideColumns(b *Battery) {
	for _, c := range b.columnsList {
		createElevatorsList(&c)
		// createButtonsUpList()
		// createButtonsDownList()
	}
}

//------------------------------------------- COLUMN ------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Column struct {
	id                         int
	name                       rune
	status                     ColumnStatus
	numberOfElevatorsPerColumn int
	minFloor                   int
	maxFloor                   int
	numberServedFloors         int
	numberOfBasements          int
	battery                    Battery
	elevatorsList              []Elevator
	buttonsUpList              []Button
	buttonsDownList            []Button
}

//----------------- Function to create Column -----------------//
func newColumn(id int, name rune, columnStatus ColumnStatus, numberOfElevatorsPerColumn int, numberServedFloors int, numberOfBasements int, battery *Battery) *Column {
	c := new(Column)
	c.id = id
	c.name = name
	c.status = columnStatus
	c.numberOfElevatorsPerColumn = numberOfElevatorsPerColumn
	c.numberServedFloors = numberServedFloors
	c.numberOfBasements = numberOfBasements
	c.battery = *battery
	c.elevatorsList = []Elevator{}
	c.buttonsUpList = []Button{}
	c.buttonsDownList = []Button{}

	return c
}

//----------------- Functions to create a list -----------------//
/* ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN ******* */
func createElevatorsList(c *Column) {
	for i := 1; i <= c.numberOfElevatorsPerColumn; i++ {
		e := newElevator(i, c.numberServedFloors, 1, elevatorIdle, sensorOff, sensorOff, c)
		c.elevatorsList = append(c.elevatorsList)
		// fmt.Println("Created elevator" + *c.name + string(e.id))
		fmt.Printf("Created elevator%v%d\n", string(c.name), e.id)
	}
}

/* ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR ******* */
func createButtonsUpList(c *Column) {
	c.buttonsUpList = append(newButton(0, buttonOff, 0))
	for i := c.minFloor; i < c.maxFloor; i++ {
		e := newButton(i, buttonOff, i)
		c.buttonsUpList = append(c.buttonsUpList)
	}
}

//------------------------------------------- ELEVATOR ----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Elevator struct {
	id                      int
	numberServedFloors      int
	floor                   int
	status                  ElevatorStatus
	weightSensorStatus      SensorStatus
	obstructionSensorStatus SensorStatus
	column                  Column
	elevatorDoor            Door
	elevatorDisplay         Display
	floorDoorsList          []Door
	floorDisplaysList       []Display
	floorButtonsList        []Button
	floorList               []int
}

//----------------- Function to create Elevator -----------------//
func newElevator(id int, numberServedFloors int, floor int, elevatorStatus ElevatorStatus, weightSensorStatus SensorStatus, obstructionSensorStatus SensorStatus, column *Column) *Elevator {
	e := new(Elevator)
	e.id = id
	e.numberServedFloors = numberServedFloors
	e.floor = floor
	e.status = elevatorStatus
	e.weightSensorStatus = weightSensorStatus
	e.obstructionSensorStatus = obstructionSensorStatus
	e.column = *column
	e.elevatorDoor = Door{0, doorClosed, 0}
	e.elevatorDisplay = Display{0, displayOn, 0}
	// e.floorDoorsList = createFloorDoorsList()
	// e.floorDisplaysList = createDisplaysList()
	// e.floorButtonsList = createFloorButtonsList()
	e.floorList = []int{}

	return e
}

//------------------------------------------- DOOR --------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Door struct {
	id     int
	status DoorStatus
	floor  int
}

func newDoor(id int, doorStatus DoorStatus, floor int) *Door {
	button := id
	button.status = doorStatus
	button.floor = floor

	return door
}

//------------------------------------------- BUTTON ------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Button struct {
	id     int
	status ButtonStatus
	floor  int
}

func newButton(id int, buttonStatus ButtonStatus, floor int) *Button {
	button := id
	button.status = buttonStatus
	button.floor = floor

	return button
}

//------------------------------------------- DISPLAY -----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Display struct {
	id     int
	status DisplayStatus
	floor  int
}

func newDisplay(id int, displayStatus DisplayStatus, floor int) *Display {
	button := id
	button.status = displayStatus
	button.floor = floor

	return display
}

// ------------------------------------------- CONSTANTS (as enums) ---------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* BATTERY STATUS ******* */
type BatteryStatus string

const (
	batteryActive   BatteryStatus = "Active"
	batteryInactive               = "Inactive"
)

/* ******* COLUMN ******* */
type ColumnStatus string

const (
	columnActive   ColumnStatus = "Active"
	columnInactive              = "Inactive"
)

/* ******* ELEVATOR STATUS ******* */
type ElevatorStatus string

const (
	elevatorIdle ElevatorStatus = "Idle"
	elevatorUp                  = "Up"
	elevatorDown                = "Down"
)

/* ******* BUTTONS STATUS ******* */
type ButtonStatus string

const (
	buttonOn  ButtonStatus = "On"
	buttonOff              = "Off"
)

/* ******* SENSORS STATUS ******* */
type SensorStatus string

const (
	sensorOn  SensorStatus = "On"
	sensorOff              = "Off"
)

/* ******* DOORS STATUS ******* */
type DoorStatus string

const (
	doorOpened DoorStatus = "Opened"
	doorClosed            = "Closed"
)

/* ******* DISPLAY STATUS ******* */
type DisplayStatus string

const (
	displayOn  DisplayStatus = "On"
	displayOff               = "Off"
)

/* ******* REQUESTED DIRECTION ******* */
type Direction string

const (
	directionOn  Direction = "Up"
	directionOff           = "Down"
)

//------------------------------------------- TESTING PROGRAM - SCENARIOS ---------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* CREATE SCENARIO 1 ******* */
func scenario1() {

}

func main() {
	/* ******* CALL SCENARIOS ******* */
	battery1 := newBattery(1, 4, 66, 6, 5, batteryActive)
	fmt.Printf("Created battery%d%v\n", battery1.id, string(battery1.columnsList[0].name))
	// scenario1()
	// scenario2()
	// scenario3()
	// scenario4()
}
