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
	b.id
	b.numberOfColumns = numberOfColumns
	b.totalNumberOfFloors = totalNumberOfFloors
	b.numberOfBasements = numberOfBasements
	b.numberOfElevatorsPerColumn = numberOfElevatorsPerColumn
	b.status = batteryStatus
	b.columnsList = []Column{}
	b.numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn()
	b.createColumnsList()
	b.setColumnValues()
	b.createListsInsideColumns()
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
func newColumn(id int, name rune, columnStatus ColumnStatus, numberOfElevatorsPerColumn int, numberServedFloors int, numberOfBasements int, battery Battery) *Column {
	c := new(Column)
	c.id = id
	c.name = name
	c.status = columnStatus
	c.numberOfElevatorsPerColumn = numberOfElevatorsPerColumn
	c.numberServedFloors = numberServedFloors
	c.numberOfBasements = numberOfBasements
	c.battery = battery
	c.elevatorsList = []Elevator{}
	c.buttonsUpList = []Button{}
	c.buttonsDownList = []Button{}
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
func newElevator(id int, numberServedFloors int, floor int, elevatorStatus ElevatorStatus, weightSensorStatus SensorStatus, obstructionSensorStatus SensorStatus, column Column) *Elevator {
	e := new(Elevator)
	e.id = id
	e.numberServedFloors = numberServedFloors
	e.floor = floor
	e.status = elevatorStatus
	e.weightSensorStatus = weightSensorStatus
	e.obstructionSensorStatus = obstructionSensorStatus
	e.column = column
	e.elevatorDoor = Door{0, doCLOSED, 0}
	e.elevatorDisplay = Display{0, diON, 0}
	e.floorDoorsList = []Door{}
	e.floorDisplaysList = []Display{}
	e.floorButtonsList = []Button{}
	e.floorList = []Button{}
	e.createFloorDoorsList()
	e.createDisplaysList()
	e.createFloorButtonsList()
}

//------------------------------------------- DOOR --------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Door struct {
	id     int
	status DoorStatus
	floor  int
}

//------------------------------------------- BUTTON ------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Button struct {
	id     int
	status ButtonStatus
	floor  int
}

//------------------------------------------- DISPLAY -----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Display struct {
	id     int
	status DisplayStatus
	floor  int
}

// ------------------------------------------- CONSTANTS (as enums) ---------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* BATTERY STATUS ******* */
type BatteryStatus string

const (
	baACTIVE   BatteryStatus = "Active"
	baINACTIVE               = "Inactive"
)

/* ******* COLUMN ******* */
type ColumnStatus string

const (
	coACTIVE   ColumnStatus = "Active"
	coINACTIVE              = "Inactive"
)

/* ******* ELEVATOR STATUS ******* */
type ElevatorStatus string

const (
	elIDLE ElevatorStatus = "Idle"
	elUP                  = "Up"
	elDOWN                = "Down"
)

/* ******* BUTTONS STATUS ******* */
type ButtonStatus string

const (
	buON  ButtonStatus = "On"
	buOFF              = "Off"
)

/* ******* SENSORS STATUS ******* */
type SensorStatus string

const (
	seON  SensorStatus = "On"
	seOFF              = "Off"
)

/* ******* DOORS STATUS ******* */
type DoorStatus string

const (
	doOPENED DoorStatus = "Opened"
	doCLOSED            = "Closed"
)

/* ******* DISPLAY STATUS ******* */
type DisplayStatus string

const (
	diON  DisplayStatus = "On"
	diOFF               = "Off"
)

/* ******* REQUESTED DIRECTION ******* */
type Direction string

const (
	diUP   Direction = "Up"
	diDOWN           = "Down"
)

// func soma(x int, y int) int { //last int is the return type, for more than 1 return use: func soma(x int) int (int, int) {}
// 	return x + y
// }

func main() {
	// var nome string = "Cindy"
	// fmt.Println("Hello, " + nome)
	// fmt.Println(soma(3, 4))
}
