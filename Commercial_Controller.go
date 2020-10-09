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
 totalNumberOfFloors = numberOfFloors + math.Abs(numberOfBasements) //Transform the number of basements to a positive number
 minBuildingFloor                                                   //Is equal to 1 OR equal the numberOfBasements if there is a basement
 maxBuildingFloor = numberOfFloors                                  //Is the last floor of the building
 maxWeight                                                          //Maximum weight an elevator can carry in KG

** ************************************************** */

package main

import (
	"fmt"
	"math"
)

//------------------------------------------- BATTERY -----------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
type Battery struct {
	id                         int
	numberOfColumns            int
	minBuildingFloor           int //Is equal to 1 OR equal the numberOfBasements if there is a basement
	maxBuildingFloor           int //Is the last floor of the building
	numberOfFloors             int //Floors of the building excluding the number of basements
	numberOfBasements          int
	totalNumberOfFloors        int //numberOfFloors + math.Abs(numberOfBasements)
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
	fmt.Printf("battery%d | Basements: %d | Columns: %d | Elevators per column: %d\n", b.id, b.numberOfBasements, b.numberOfColumns, b.numberOfElevatorsPerColumn)

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
		createButtonsDownList(&c)
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
	fmt.Printf("column%v | Served floors: %d | Min floor: %d | Max floor: %d\n", c.name, c.numberServedFloors, c.minFloor, c.maxFloor)

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
	// fmt.Printf("Created buttons UP list - column%v\n", string(c.name))
}

/* ******* CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR ******* */
func createButtonsDownList(c *Column) {
	bt := newButton(1, buttonOff, 1)
	c.buttonsDownList = append(c.buttonsDownList, *bt)
	var minBuildingFloor int
	if c.numberOfBasements > 0 {
		minBuildingFloor = c.numberOfBasements * -1
	} else {
		minBuildingFloor = 1
	}
	for i := minBuildingFloor + 1; i <= c.maxFloor; i++ {
		bt = newButton(i, buttonOff, i)
		c.buttonsDownList = append(c.buttonsDownList, *bt)
	}
	// fmt.Printf("Created buttons DOWN list - column%v\n", string(c.name))
}

//----------------- Functions for logic -----------------//
/* ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC ******* */
func findElevator(currentFloor int, direction Direction, c *Column) Elevator {
	var bestElevator Elevator
	var activeElevatorList = []Elevator{}
	var idleElevatorList = []Elevator{}
	var sameDirectionElevatorList = []Elevator{}
	for _, elevator := range c.elevatorsList {
		if elevator.status != elevatorIdle {
			//Verify if the request is on the elevators way, otherwise the elevator will just continue its way ignoring this call
			if elevator.status == elevatorUp && elevator.floor <= currentFloor || elevator.status == elevatorDown && elevator.floor >= currentFloor {
				activeElevatorList = append(activeElevatorList, elevator)
			}
		} else {
			idleElevatorList = append(idleElevatorList, elevator)
		}
	}

	if len(activeElevatorList) > 0 { //Create new list for elevators with same direction that the request
		for _, elevator := range activeElevatorList {
			if string(elevator.status) == string(direction) {
				sameDirectionElevatorList = append(sameDirectionElevatorList, elevator)
			}
		}
	}

	if len(sameDirectionElevatorList) > 0 {
		bestElevator = findNearestElevator(currentFloor, sameDirectionElevatorList, c) // 1- Try to use an elevator that is moving and has the same direction
	} else if len(idleElevatorList) > 0 {
		bestElevator = findNearestElevator(currentFloor, idleElevatorList, c) // 2- Try to use an elevator that is IDLE
	} else {
		bestElevator = findNearestElevator(currentFloor, activeElevatorList, c) // 3- As the last option, uses an elevator that is moving at the contrary direction
	}

	return bestElevator
}

/* ******* LOGIC TO FIND THE NEAREST ELEVATOR ******* */
func findNearestElevator(currentFloor int, selectedList []Elevator, c *Column) Elevator {
	var bestElevator Elevator = selectedList[0]
	var bestDistance float64 = math.Abs(float64(selectedList[0].floor - currentFloor)) //math.Abs() returns the absolute value of a number (always positive).
	for _, elevator := range selectedList {
		if math.Abs(float64(elevator.floor-currentFloor)) < bestDistance {
			bestElevator = elevator
		}
	}
	fmt.Println("\n-----------------------------------------------------")
	fmt.Printf("   > > >> >>> ELEVATOR%v%d WAS CALLED <<< << < <\n", string(c.name), bestElevator.id)
	fmt.Println("-----------------------------------------------------\n")

	return bestElevator
}

/* ******* LOGIC TO TURN ON THE BUTTONS FOR THE ASKED DIRECTION ******* */
func manageButtonStatusOn(requestedFloor int, direction Direction, c *Column) {
	var currentButton Button
	if direction == directionUp {
		for _, button := range c.buttonsUpList {
			if button.id == requestedFloor { //find the UP button by ID
				currentButton = button
			}
		}
	} else {
		for _, button := range c.buttonsDownList {
			if button.id == requestedFloor { //find the DOWN button by ID
				currentButton = button
			}
		}
	}
	currentButton.status = buttonOn
}

//----------------- Entry method -----------------//
/* ******* ENTRY METHOD ******* */
/* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR ******* */
func requestElevator(requestedFloor int, direction Direction, c *Column) { // User goes to the specific column and press a button outside the elevator requesting for an elevator
	manageButtonStatusOn(requestedFloor, direction, c)
	var bestElevator Elevator = c.findElevator(requestedFloor, direction)
	if bestElevator.floor != requestedFloor {
		bestElevator.addFloorToFloorList(requestedFloor)
		bestElevator.moveElevator(requestedFloor)
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
	e.floorDoorsList = createFloorDoorsList()
	e.floorDisplaysList = createDisplaysList()
	e.floorButtonsList = createFloorButtonsList()
	e.floorList = []int{}
	fmt.Printf("elevator%v%d | Floor: %d | Status: %d\n", string(column.name), e.id, e.floor, e.status)

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
	directionUp   Direction = "Up"
	directionDown           = "Down"
)

//------------------------------------------- TESTING PROGRAM - SCENARIOS ---------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* CREATE SCENARIO 1 ******* */
func scenario1() {
	fmt.Println("\n****************************** SCENARIO 1: ******************************")
	fmt.Println()
	batteryScenario1 := newBattery(1, 4, 66, 6, 5, batteryActive)
	fmt.Println(batteryScenario1)
	fmt.Println()
	for _, column := range batteryScenario1.columnsList {
		fmt.Println(column)
	}
	fmt.Println()
	//--------- ElevatorB1 ---------
	batteryScenario1.columnsList[1].elevatorsList[0].floor = 20
	batteryScenario1.columnsList[1].elevatorsList[0].status = elevatorDown
	batteryScenario1.columnsList[1].elevatorsList[0].addFloorToFloorList(5)

	//--------- ElevatorB2 ---------
	batteryScenario1.columnsList[1].elevatorsList[1].floor = 3
	batteryScenario1.columnsList[1].elevatorsList[1].status = elevatorUp
	batteryScenario1.columnsList[1].elevatorsList[1].addFloorToFloorList(15)

	//--------- ElevatorB3 ---------
	batteryScenario1.columnsList[1].elevatorsList[2].floor = 13
	batteryScenario1.columnsList[1].elevatorsList[2].status = elevatorDown
	batteryScenario1.columnsList[1].elevatorsList[2].addFloorToFloorList(1)

	//--------- ElevatorB4 ---------
	batteryScenario1.columnsList[1].elevatorsList[3].floor = 15
	batteryScenario1.columnsList[1].elevatorsList[3].status = elevatorDown
	batteryScenario1.columnsList[1].elevatorsList[3].addFloorToFloorList(2)

	//--------- ElevatorB5 ---------
	batteryScenario1.columnsList[1].elevatorsList[4].floor = 6
	batteryScenario1.columnsList[1].elevatorsList[4].status = elevatorDown
	batteryScenario1.columnsList[1].elevatorsList[4].addFloorToFloorList(1)

	for _, elevator := range batteryScenario1.columnsList[1].elevatorsList {
		fmt.Println(elevator)
	}
	fmt.Println()
	fmt.Println("Person 1: (elevator B5 is expected)") //elevator expected
	fmt.Println(">> User request an elevator from floor <1> and direction <UP> <<")
	fmt.Println(">> User request to go to floor <20>")
	batteryScenario1.columnsList[1].requestElevator(1, directionUp)   //parameters (requestedFloor, directionUp/directionDown)
	batteryScenario1.columnsList[1].elevatorsList[4].requestFloor(20) //parameters (requestedFloor)
	fmt.Println("=========================================================================")
}

/* ******* CREATE SCENARIO 2 ******* */

func main() {
	/* ******* CALL SCENARIOS ******* */
	scenario1()
	// scenario2()
	// scenario3()
	// scenario4()
}
