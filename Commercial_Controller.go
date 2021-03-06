/* *********************************************** **
 @Author		Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 0- BATTERY
    0a- Function to create Battery
    0b- Functions to create a list
    0c- Functions for logic
 1- COLUMN
    1a- Function to create Column
    1b- Functions to create a list
    1c- Functions for logic
    1d- Entry Function
 2- ELEVATOR
    2a- Function to create Elevator
    2b- Functions to create a list
    2c- Functions for logic
    2d- Entry Function
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
	"math/rand"
	"sort"
	"time"
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
	columnsList                []*Column
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
	b.columnsList = []*Column{}
	b.numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn(b)
	createColumnsList(b)
	setColumnValues(b)
	fmt.Printf("battery%d | Basements: %d | Columns: %d | Elevators per column: %d\n", b.id, b.numberOfBasements, b.numberOfColumns, b.numberOfElevatorsPerColumn)

	return b
}

//----------------- Functions to create a list -----------------//
/* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
func createColumnsList(b *Battery) {
	name := 'A'
	for i := 1; i <= b.numberOfColumns; i++ {
		c := newColumn(i, name, columnActive, b.numberOfElevatorsPerColumn, b.numberOfFloorsPerColumn, b.numberOfBasements, b)
		b.columnsList = append(b.columnsList, c)
		name++
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
	elevatorsList              []*Elevator
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
	c.elevatorsList = []*Elevator{}
	c.buttonsUpList = []Button{}
	c.buttonsDownList = []Button{}
	createElevatorsList(c)
	createButtonsUpList(c)
	createButtonsDownList(c)
	// fmt.Printf("column%v | Served floors: %d | Min floor: %d | Max floor: %d\n", string(c.name), c.numberServedFloors, c.minFloor, c.maxFloor)

	return c
}

//----------------- Functions to create a list -----------------//
/* ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN ******* */
func createElevatorsList(c *Column) {
	for i := 1; i <= c.numberOfElevatorsPerColumn; i++ {
		e := newElevator(i, c.numberServedFloors, 1, elevatorIdle, sensorOff, sensorOff, c)
		c.elevatorsList = append(c.elevatorsList, e)
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
func findElevator(currentFloor int, direction Direction, c *Column) *Elevator {
	bestElevator := c.elevatorsList[0]
	activeElevatorList := []*Elevator{}
	idleElevatorList := []*Elevator{}
	sameDirectionElevatorList := []*Elevator{}
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
func findNearestElevator(currentFloor int, selectedList []*Elevator, c *Column) *Elevator {
	var bestElevator *Elevator = selectedList[0]
	var bestDistance float64 = math.Abs(float64(selectedList[0].floor - currentFloor)) //math.Abs() returns the absolute value of a number (always positive).
	for _, elevator := range selectedList {
		if math.Abs(float64(elevator.floor-currentFloor)) < bestDistance {
			bestElevator = elevator
		}
	}
	fmt.Printf("elevator%s%d | Floor: %d | Status: %s\n", string(c.name), bestElevator.id, bestElevator.floor, bestElevator.status)
	fmt.Println()
	fmt.Println("\n-----------------------------------------------------")
	fmt.Printf("   > > >> >>> ELEVATOR %v%d WAS CALLED <<< << < <\n", string(c.name), bestElevator.id)
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

//----------------- Entry Function -----------------//
/* ******* ENTRY FUNCTION ******* */
/* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR ******* */
func requestElevator(requestedFloor int, direction Direction, c *Column) { // User goes to the specific column and press a button outside the elevator requesting for an elevator
	manageButtonStatusOn(requestedFloor, direction, c)
	bestElevator := findElevator(requestedFloor, direction, c)
	if bestElevator.floor != requestedFloor {
		addFloorToFloorList(requestedFloor, bestElevator)
		bestElevator = moveElevator(requestedFloor, bestElevator)
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
	createFloorDoorsList(e)
	createDisplaysList(e)
	createFloorButtonsList(e)
	e.floorList = []int{}

	return e
}

//----------------- Functions to create a list -----------------//
/* ******* CREATE A LIST WITH A DOOR OF EACH FLOOR ******* */
func createFloorDoorsList(e *Elevator) {
	door := newDoor(1, doorClosed, 1)
	e.floorDoorsList = append(e.floorDoorsList, *door)
	for i := e.column.minFloor; i <= e.column.maxFloor; i++ {
		door = newDoor(i, doorClosed, i)
		e.floorDoorsList = append(e.floorDoorsList, *door)
	}
}

/* ******* CREATE A LIST WITH A DISPLAY OF EACH FLOOR ******* */
func createDisplaysList(e *Elevator) {
	display := newDisplay(1, displayOn, 1)
	e.floorDisplaysList = append(e.floorDisplaysList, *display)
	for i := e.column.minFloor; i <= e.column.maxFloor; i++ {
		display = newDisplay(i, displayOn, i)
		e.floorDisplaysList = append(e.floorDisplaysList, *display)
	}
}

/* ******* CREATE A LIST WITH A BUTTON OF EACH FLOOR ******* */
func createFloorButtonsList(e *Elevator) {
	button := newButton(1, buttonOff, 1)
	e.floorButtonsList = append(e.floorButtonsList, *button)
	for i := e.column.minFloor; i <= e.column.maxFloor; i++ {
		button = newButton(i, buttonOff, i)
		e.floorButtonsList = append(e.floorButtonsList, *button)
	}
}

//----------------- Functions for logic -----------------//
/* ******* LOGIC TO MOVE ELEVATOR ******* */
func moveElevator(requestedFloor int, e *Elevator) *Elevator {
	for len(e.floorList) > 0 {
		if e.status == elevatorIdle {
			if e.floor < requestedFloor {
				e.status = elevatorUp
			} else if e.floor > requestedFloor {
				e.status = elevatorDown
			} else {
				openDoors(e)
				deleteFloorFromList(requestedFloor, e)
				manageButtonStatusOff(requestedFloor, e)
			}
		}
		if e.status == elevatorUp {
			e = moveUp(e)
		} else if e.status == elevatorDown {
			e = moveDown(e)
		}
	}
	return e
}

/* ******* LOGIC TO MOVE UP ******* */
func moveUp(e *Elevator) *Elevator {
	tempArray := e.floorList
	for i := e.floor; i < tempArray[len(tempArray)-1]; i++ {
		currentDoor := findDoorFromDoorsListById(e.floor, &e.floorDoorsList) // finding doors by id
		if currentDoor != nil && currentDoor.status == doorOpened || e.elevatorDoor.status == doorOpened {
			fmt.Println("   Doors are open, closing doors before move up")
			closeDoors(e)
		}
		fmt.Printf("Moving elevator%s%d <up> from floor %d to floor %d\n", string(e.column.name), e.id, i, (i + 1))
		nextFloor := (i + 1)
		e.floor = nextFloor
		updateDisplays(e.floor, e)

		if contains(tempArray, nextFloor) {
			openDoors(e)
			deleteFloorFromList(nextFloor, e)
			manageButtonStatusOff(nextFloor, e)
		}
	}
	if len(e.floorList) == 0 {
		e.status = elevatorIdle
	} else {
		e.status = elevatorDown
		fmt.Println("       Elevator is now going " + e.status)
	}
	return e
}

//Stackoverflow array.contains implementation
func contains(s []int, e int) bool {
	for _, a := range s {
		if a == e {
			return true
		}
	}
	return false
}

/* ******* LOGIC TO MOVE DOWN ******* */
func moveDown(e *Elevator) *Elevator {
	tempArray := e.floorList
	for i := e.floor; i > tempArray[len(tempArray)-1]; i-- {
		currentDoor := findDoorFromDoorsListById(e.floor, &e.floorDoorsList) // finding doors by id
		if currentDoor != nil && currentDoor.status == doorOpened || e.elevatorDoor.status == doorOpened {
			fmt.Println("   Doors are open, closing doors before move down")
			closeDoors(e)
		}
		fmt.Printf("Moving elevator%s%d <down> from floor %d to floor %d\n", string(e.column.name), e.id, i, (i - 1))
		nextFloor := (i - 1)
		e.floor = nextFloor
		updateDisplays(e.floor, e)

		if contains(tempArray, nextFloor) {
			openDoors(e)
			deleteFloorFromList(nextFloor, e)
			manageButtonStatusOff(nextFloor, e)
		}
	}
	if len(e.floorList) == 0 {
		e.status = elevatorIdle
	} else {
		e.status = elevatorUp
		fmt.Println("       Elevator is now going " + e.status)
	}

	return e
}

/* ******* LOGIC TO FIND BUTTONS BY ID AND SET BUTTON STATUS OFF ******* */
func manageButtonStatusOff(floor int, e *Elevator) {
	currentUpButton := findButtonFromButtonsListById(floor, &e.column.buttonsUpList)
	if currentUpButton != nil {
		currentUpButton.status = buttonOff
	}
	currentDownButton := findButtonFromButtonsListById(floor, &e.column.buttonsDownList)
	if currentDownButton != nil {
		currentDownButton.status = buttonOff
	}
	currentFloorButton := findButtonFromButtonsListById(floor, &e.floorButtonsList)
	if currentFloorButton != nil {
		currentFloorButton.status = buttonOff
	}
}

func findButtonFromButtonsListById(id int, buttonList *[]Button) *Button {
	for _, button := range *buttonList {
		if button.id == id {
			return &button
		}
	}
	return nil
}

/* ******* LOGIC TO FIND DOOR BY ID ******* */
func findDoorFromDoorsListById(id int, doorList *[]Door) *Door {
	for _, door := range *doorList {
		if door.id == id {
			return &door
		}
	}
	return nil
}

/* ******* LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR ******* */
func updateDisplays(elevatorFloor int, e *Elevator) {
	for _, display := range e.floorDisplaysList {
		display.floor = elevatorFloor
	}
}

/* ******* LOGIC TO OPEN DOORS ******* */
func openDoors(e *Elevator) {
	fmt.Printf("       Elevator is stopped at floor %d\n", e.floor)
	fmt.Println("       Opening doors...")
	fmt.Println("       Elevator doors are opened")
	e.elevatorDoor.status = doorOpened
	currentDoor := findDoorFromDoorsListById(e.floor, &e.floorDoorsList) //filter floor door by ID and set status to OPENED
	if currentDoor != nil {
		currentDoor.status = doorOpened
	}

	time.Sleep(1 * time.Second) //How many time the door remains opened in SECONDS - I use 1 second so the scenarios test will run faster
	closeDoors(e)
}

/* ******* LOGIC TO CLOSE DOORS ******* */
func closeDoors(e *Elevator) {
	checkWeight(e)
	checkObstruction(e)
	if e.weightSensorStatus == sensorOff && e.obstructionSensorStatus == sensorOff { //Security logic
		fmt.Println("       Closing doors...")
		fmt.Println("       Elevator doors are closed")
		currentDoor := findDoorFromDoorsListById(e.floor, &e.floorDoorsList) //filter floor door by ID and set status to OPENED
		if currentDoor != nil {
			currentDoor.status = doorClosed
		}
		e.elevatorDoor.status = doorClosed
	}
}

/* ******* LOGIC FOR WEIGHT SENSOR ******* */
func checkWeight(e *Elevator) {
	maxWeight := 500                           //Maximum weight an elevator can carry in KG
	randomWeight := rand.Intn(100) + maxWeight //This random simulates the weight from a weight sensor
	for randomWeight > maxWeight {             //Logic of loading
		e.weightSensorStatus = sensorOn //Detect a full elevator
		fmt.Println("       ! Elevator capacity reached, waiting until the weight is lower before continue...")
		randomWeight -= 100 //I'm supposing the random number is 600, I'll subtract 101 so it will be less than 500 (the max weight I proposed) for the second time it runs
	}
	e.weightSensorStatus = sensorOff
	fmt.Println("       Elevator capacity is OK")
}

/* ******* LOGIC FOR OBSTRUCTION SENSOR ******* */
func checkObstruction(e *Elevator) {
	probabilityNotBlocked := 70
	number := rand.Intn(100) //This random simulates the probability of an obstruction (I supposed 30% of chance something is blocking the door)
	for number > probabilityNotBlocked {
		e.obstructionSensorStatus = sensorOn
		fmt.Println("       ! Elevator door is blocked by something, waiting until door is free before continue...")
		number -= 30 //I'm supposing the random number is 100, I'll subtract 30 so it will be less than 70 (30% probability), so the second time it runs theres no one blocking the door
	}
	e.obstructionSensorStatus = sensorOff
	fmt.Println("       Elevator door is FREE")
}

/* ******* LOGIC TO ADD A FLOOR TO THE FLOOR LIST ******* */
func addFloorToFloorList(floor int, e *Elevator) {
	if !contains(e.floorList, floor) {
		e.floorList = append(e.floorList, floor)
		sort.Slice(e.floorList, func(i, j int) bool { //Order list ascending
			return e.floorList[i] < e.floorList[j]
		})
	}
}

/* ******* LOGIC TO DELETE ITEM FROM FLOORS LIST ******* */
func deleteFloorFromList(stopFloor int, e *Elevator) {
	i := indexOf(len(e.floorList), func(i int) bool { return e.floorList[i] == stopFloor })

	if i > -1 {
		e.floorList = remove(e.floorList, i)
	}
}

// Code from stackoverflow to remove an item from array from given index
func remove(slice []int, s int) []int {
	return append(slice[:s], slice[s+1:]...)
}

// Code from stackoverflow to get index of an array
func indexOf(limit int, predicate func(i int) bool) int {
	for i := 0; i < limit; i++ {
		if predicate(i) {
			return i
		}
	}
	return -1
}

//----------------- Entry Function -----------------//
/* ******* ENTRY FUNCTION ******* */
/* ******* REQUEST FOR A FLOOR BY PRESSING THE FLOOR BUTTON INSIDE THE ELEVATOR ******* */
func requestFloor(requestedFloor int, e *Elevator) {
	if e.floor != requestedFloor {
		addFloorToFloorList(requestedFloor, e)
		moveElevator(requestedFloor, e)
	}
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

/* ******* COLUMN STATUS******* */
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
	fmt.Println()
	for _, column := range batteryScenario1.columnsList {
		fmt.Printf("column%v | Served floors: %d | Min floor: %d | Max floor: %d\n", string(column.name), column.numberServedFloors, column.minFloor, column.maxFloor)
	}
	fmt.Println()
	//--------- ElevatorB1 ---------
	batteryScenario1.columnsList[1].elevatorsList[0].floor = 20
	batteryScenario1.columnsList[1].elevatorsList[0].status = elevatorDown
	addFloorToFloorList(5, batteryScenario1.columnsList[1].elevatorsList[0])

	//--------- ElevatorB2 ---------
	batteryScenario1.columnsList[1].elevatorsList[1].floor = 3
	batteryScenario1.columnsList[1].elevatorsList[1].status = elevatorUp
	addFloorToFloorList(15, batteryScenario1.columnsList[1].elevatorsList[1])

	//--------- ElevatorB3 ---------
	batteryScenario1.columnsList[1].elevatorsList[2].floor = 13
	batteryScenario1.columnsList[1].elevatorsList[2].status = elevatorDown
	addFloorToFloorList(1, batteryScenario1.columnsList[1].elevatorsList[2])

	//--------- ElevatorB4 ---------
	batteryScenario1.columnsList[1].elevatorsList[3].floor = 15
	batteryScenario1.columnsList[1].elevatorsList[3].status = elevatorDown
	addFloorToFloorList(2, batteryScenario1.columnsList[1].elevatorsList[3])

	//--------- ElevatorB5 ---------
	batteryScenario1.columnsList[1].elevatorsList[4].floor = 6
	batteryScenario1.columnsList[1].elevatorsList[4].status = elevatorDown
	addFloorToFloorList(1, batteryScenario1.columnsList[1].elevatorsList[4])

	for _, elevator := range batteryScenario1.columnsList[1].elevatorsList {
		fmt.Printf("elevator%s%d | Floor: %d | Status: %s\n", string(batteryScenario1.columnsList[1].name), elevator.id, elevator.floor, elevator.status)
	}
	fmt.Println()
	fmt.Println("Person 1: (elevator B5 is expected)") //elevator expected
	fmt.Println(">> User request an elevator from floor <1> and direction <UP> <<")
	fmt.Println(">> User request to go to floor <20>")
	requestElevator(1, directionUp, batteryScenario1.columnsList[1])   //parameters (requestedFloor, directionUp/directionDown)
	requestFloor(20, batteryScenario1.columnsList[1].elevatorsList[4]) //parameters (requestedFloor)
	fmt.Println("=========================================================================")
}

/* ******* CREATE SCENARIO 2 ******* */
func scenario2() {
	fmt.Println("\n****************************** SCENARIO 2: ******************************")
	fmt.Println()
	batteryScenario2 := newBattery(1, 4, 66, 6, 5, batteryActive)
	fmt.Println()
	for _, column := range batteryScenario2.columnsList {
		fmt.Printf("column%v | Served floors: %d | Min floor: %d | Max floor: %d\n", string(column.name), column.numberServedFloors, column.minFloor, column.maxFloor)
	}
	fmt.Println()
	//--------- ElevatorC1 ---------;
	batteryScenario2.columnsList[2].elevatorsList[0].floor = 1
	batteryScenario2.columnsList[2].elevatorsList[0].status = elevatorUp
	addFloorToFloorList(21, batteryScenario2.columnsList[2].elevatorsList[0]) //not departed yet

	//--------- ElevatorC2 ---------
	batteryScenario2.columnsList[2].elevatorsList[1].floor = 23
	batteryScenario2.columnsList[2].elevatorsList[1].status = elevatorUp
	addFloorToFloorList(28, batteryScenario2.columnsList[2].elevatorsList[1])

	//--------- ElevatorC3 ---------
	batteryScenario2.columnsList[2].elevatorsList[2].floor = 33
	batteryScenario2.columnsList[2].elevatorsList[2].status = elevatorDown
	addFloorToFloorList(1, batteryScenario2.columnsList[2].elevatorsList[2])

	//--------- ElevatorC4 ---------
	batteryScenario2.columnsList[2].elevatorsList[3].floor = 40
	batteryScenario2.columnsList[2].elevatorsList[3].status = elevatorDown
	addFloorToFloorList(24, batteryScenario2.columnsList[2].elevatorsList[3])

	//--------- ElevatorC5 ---------
	batteryScenario2.columnsList[2].elevatorsList[4].floor = 39
	batteryScenario2.columnsList[2].elevatorsList[4].status = elevatorDown
	addFloorToFloorList(1, batteryScenario2.columnsList[2].elevatorsList[4])

	for _, elevator := range batteryScenario2.columnsList[2].elevatorsList {
		fmt.Printf("elevator%s%d | Floor: %d | Status: %s\n", string(batteryScenario2.columnsList[2].name), elevator.id, elevator.floor, elevator.status)
	}
	fmt.Println()
	fmt.Println("Person 1: (elevator C1 is expected)") //elevator expected
	fmt.Println(">> User request an elevator from floor <1> and direction <UP> <<")
	fmt.Println(">> User request to go to floor <36>")
	requestElevator(1, directionUp, batteryScenario2.columnsList[2])   //parameters (requestedFloor, buttonDirection.UP/DOWN)
	requestFloor(36, batteryScenario2.columnsList[2].elevatorsList[0]) //parameters (requestedFloor)
	fmt.Println("=========================================================================")
}

/* ******* CREATE SCENARIO 3 ******* */
func scenario3() {
	fmt.Println("\n****************************** SCENARIO 3: ******************************")
	fmt.Println()
	batteryScenario3 := newBattery(1, 4, 66, 6, 5, batteryActive)
	fmt.Println()
	for _, column := range batteryScenario3.columnsList {
		fmt.Printf("column%v | Served floors: %d | Min floor: %d | Max floor: %d\n", string(column.name), column.numberServedFloors, column.minFloor, column.maxFloor)
	}
	fmt.Println()

	//--------- ElevatorD1 ---------
	batteryScenario3.columnsList[3].elevatorsList[0].floor = 58
	batteryScenario3.columnsList[3].elevatorsList[0].status = elevatorDown
	addFloorToFloorList(1, batteryScenario3.columnsList[3].elevatorsList[0])

	//--------- ElevatorD2 ---------
	batteryScenario3.columnsList[3].elevatorsList[1].floor = 50
	batteryScenario3.columnsList[3].elevatorsList[1].status = elevatorUp
	addFloorToFloorList(60, batteryScenario3.columnsList[3].elevatorsList[1])

	//--------- ElevatorD3 ---------
	batteryScenario3.columnsList[3].elevatorsList[2].floor = 46
	batteryScenario3.columnsList[3].elevatorsList[2].status = elevatorUp
	addFloorToFloorList(58, batteryScenario3.columnsList[3].elevatorsList[2])

	//--------- ElevatorD4 ---------
	batteryScenario3.columnsList[3].elevatorsList[3].floor = 1
	batteryScenario3.columnsList[3].elevatorsList[3].status = elevatorUp
	addFloorToFloorList(54, batteryScenario3.columnsList[3].elevatorsList[3])

	//--------- ElevatorD5 ---------
	batteryScenario3.columnsList[3].elevatorsList[4].floor = 60
	batteryScenario3.columnsList[3].elevatorsList[4].status = elevatorDown
	addFloorToFloorList(1, batteryScenario3.columnsList[3].elevatorsList[4])

	for _, elevator := range batteryScenario3.columnsList[3].elevatorsList {
		fmt.Printf("elevator%s%d | Floor: %d | Status: %s\n", string(batteryScenario3.columnsList[3].name), elevator.id, elevator.floor, elevator.status)
	}
	fmt.Println()
	fmt.Println("Person 1: (elevator D1 is expected)") //elevator expected
	fmt.Println(">> User request an elevator from floor <54> and direction <DOWN> <<")
	fmt.Println(">> User request to go to floor <1>")
	requestElevator(54, directionDown, batteryScenario3.columnsList[3]) //parameters (requestedFloor, buttonDirection.UP/DOWN)
	requestFloor(1, batteryScenario3.columnsList[3].elevatorsList[0])   //parameters (requestedFloor)
	fmt.Println("=========================================================================")
}

/* ******* CREATE SCENARIO 4 ******* */
func scenario4() {
	fmt.Println("\n****************************** SCENARIO 4: ******************************")
	fmt.Println()
	batteryScenario4 := newBattery(1, 4, 66, 6, 5, batteryActive)
	fmt.Println()
	for _, column := range batteryScenario4.columnsList {
		fmt.Printf("column%v | Served floors: %d | Min floor: %d | Max floor: %d\n", string(column.name), column.numberServedFloors, column.minFloor, column.maxFloor)
	}
	fmt.Println()

	//--------- ElevatorA1 ---------
	batteryScenario4.columnsList[0].elevatorsList[0].floor = -4 //use of negative numbers to indicate SS / basement
	batteryScenario4.columnsList[0].elevatorsList[0].status = elevatorIdle

	//--------- ElevatorA2 ---------
	batteryScenario4.columnsList[0].elevatorsList[1].floor = 1
	batteryScenario4.columnsList[0].elevatorsList[1].status = elevatorIdle

	//--------- ElevatorA3 ---------
	batteryScenario4.columnsList[0].elevatorsList[2].floor = -3 //use of negative numbers to indicate SS / basement
	batteryScenario4.columnsList[0].elevatorsList[2].status = elevatorDown
	addFloorToFloorList(-5, batteryScenario4.columnsList[0].elevatorsList[2])

	//--------- ElevatorA4 ---------
	batteryScenario4.columnsList[0].elevatorsList[3].floor = -6 //use of negative numbers to indicate SS / basement
	batteryScenario4.columnsList[0].elevatorsList[3].status = elevatorUp
	addFloorToFloorList(1, batteryScenario4.columnsList[0].elevatorsList[3])

	//--------- ElevatorA5 ---------
	batteryScenario4.columnsList[0].elevatorsList[4].floor = -1 //use of negative numbers to indicate SS / basement
	batteryScenario4.columnsList[0].elevatorsList[4].status = elevatorDown
	addFloorToFloorList(-6, batteryScenario4.columnsList[0].elevatorsList[4]) //use of negative numbers to indicate SS / basement

	for _, elevator := range batteryScenario4.columnsList[0].elevatorsList {
		fmt.Printf("elevator%s%d | Floor: %d | Status: %s\n", string(batteryScenario4.columnsList[0].name), elevator.id, elevator.floor, elevator.status)
	}
	fmt.Println()
	fmt.Println("Person 1: (elevator A4 is expected)") //elevator expected
	fmt.Println(">> User request an elevator from floor <-3> and direction <UP> <<")
	fmt.Println(">> User request to go to floor <1>")
	requestElevator(-3, directionUp, batteryScenario4.columnsList[0]) //parameters (requestedFloor, buttonDirection.UP/DOWN)
	requestFloor(1, batteryScenario4.columnsList[0].elevatorsList[3]) //parameters (requestedFloor)
	fmt.Println("=========================================================================")
}

func main() {
	/* ******* CALL SCENARIOS ******* */
	scenario1()
	scenario2()
	scenario3()
	scenario4()
}
