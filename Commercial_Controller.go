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
	b.numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn(b)
	createColumnsList(b)
	setColumnValues(b)
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
		// fmt.Println("Created column" + string(c.name))
		name++
	}
	// fmt.Printf("Created columnsList:%v\n", b.columnsList)
}

/* ******* CALL FUNCTIONS TO CREATE THE LISTS INSIDE EACH COLUMN ******* */
func createListsInsideColumns(b *Battery) {
	for _, c := range b.columnsList {
		createElevatorsList(&c)
		createButtonsUpList(&c)
		// createButtonsDownList()
	}
}

//----------------- Functions for logic -----------------//
/* ******* LOGIC TO FIND THE FLOORS SERVED PER EACH COLUMN ******* */
func calculateNumberOfFloorsPerColumn(b *Battery) int {
	b.numberOfFloors = b.totalNumberOfFloors - b.numberOfBasements

	if b.numberOfBasements > 0 { //if there is basement floors
		b.numberOfFloorsPerColumn = (b.numberOfFloors / (b.numberOfColumns - 1)) //the first column serves the basement floors
	} else { //if there is no basement
		b.numberOfFloorsPerColumn = (b.numberOfFloors / b.numberOfColumns)
	}

	return b.numberOfFloorsPerColumn
}

/* ******* LOGIC TO FIND THE REMAINING FLOORS OF EACH COLUMN AND SET VALUES servedFloors, minFloors, maxFloors ******* */
func setColumnValues(b *Battery) {
	var remainingFloors int

	//calculating the remaining floors
	if b.numberOfBasements > 0 { //if there are basement floors
		remainingFloors = b.numberOfFloors % (b.numberOfColumns - 1)
	} else { //if there is no basement
		remainingFloors = b.numberOfFloors % b.numberOfColumns
	}

	//setting the minFloor and maxFloor of each column
	if b.numberOfColumns == 1 { //if there is just one column, it serves all the floors of the building
		initializeUniqueColumnFloors(b)
	} else { //for more than 1 column
		initializeMultiColumnFloors(b)

		//adjusting the number of served floors of the columns if there are remaining floors
		if remainingFloors != 0 { //if the remainingFloors is not zero, then it adds the remaining floors to the last column
			b.columnsList[len(b.columnsList)-1].numberServedFloors = b.numberOfFloorsPerColumn + remainingFloors
			b.columnsList[len(b.columnsList)-1].maxFloor = b.columnsList[len(b.columnsList)-1].minFloor + b.columnsList[len(b.columnsList)-1].numberServedFloors
		}
		//if there is a basement, then the first column will serve the basements + RDC
		if b.numberOfBasements > 0 {
			initializeBasementColumnFloors(b)
		}
	}
}

/* ******* LOGIC TO SET THE minFloor AND maxFloor FOR THE BASEMENT COLUMN ******* */
func initializeBasementColumnFloors(b *Battery) {
	b.columnsList[0].numberServedFloors = (b.numberOfBasements + 1) //+1 is the RDC
	b.columnsList[0].minFloor = b.numberOfBasements * -1            //the minFloor of basement is a negative number
	b.columnsList[0].maxFloor = 1                                   //1 is the RDC
}

/* ******* LOGIC TO SET THE minFloor AND maxFloor FOR ALL THE COLUMNS EXCLUDING BASEMENT COLUMN ******* */
func initializeMultiColumnFloors(b *Battery) {
	var minimumFloor = 1
	for i := 1; i < len(b.columnsList); i++ { //if its not the first column (because the first column serves the basements)
		if i == 1 {
			b.columnsList[i].numberServedFloors = b.numberOfFloorsPerColumn
		} else {
			b.columnsList[i].numberServedFloors = (b.numberOfFloorsPerColumn + 1) //Add 1 floor for the RDC/ground floor
		}
		b.columnsList[i].minFloor = minimumFloor
		b.columnsList[i].maxFloor = b.columnsList[i].minFloor + (b.numberOfFloorsPerColumn - 1)
		minimumFloor = b.columnsList[i].maxFloor + 1 //setting the minimum floor for the next column
	}
}

/* ******* LOGIC TO SET THE minFloor AND maxFloor IF THERE IS JUST ONE COLUMN ******* */
func initializeUniqueColumnFloors(b *Battery) {
	var minimumFloor = 1
	b.columnsList[0].numberServedFloors = b.totalNumberOfFloors
	if b.numberOfBasements > 0 { //if there is basement
		b.columnsList[0].minFloor = b.numberOfBasements
	} else { //if there is NO basement
		b.columnsList[0].minFloor = minimumFloor
		b.columnsList[0].maxFloor = b.numberOfFloors
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
		c.elevatorsList = append(c.elevatorsList, *e)
		// fmt.Printf("Created elevator%v%d\n", string(c.name), e.id)
	}
}

/* ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR ******* */
func createButtonsUpList(c *Column) {
	bt := newButton(1, buttonOff, 1)
	c.buttonsUpList = append(c.buttonsUpList, *bt)
	for i := c.minFloor; i <= c.maxFloor; i++ {
		bt = newButton(i, buttonOff, i)
		c.buttonsUpList = append(c.buttonsUpList, *bt)
	}
	// fmt.Printf("Created column%v buttonsUpList:\n", string(c.name))
	// fmt.Printf("%v\n", c.buttonsUpList)
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
	door := new(Door)
	door.id = id
	door.status = doorStatus
	door.floor = floor

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
	button := new(Button)
	button.id = id
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
	display := new(Display)
	display.id = id
	display.status = displayStatus
	display.floor = floor

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
