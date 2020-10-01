
''' ***************************************************
	@Author			Cindy Okino
	@Website		https://github.com/cindyokino
	@Last Update	October 2, 2020
    
    
SUMMARY:
1- GLOBAL VARIABLES
2- COLUMN CLASS
    2a- Constructor and its attributes
    2b- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    2c- Methods for logic: findElevator, findNearestElevator
    2d- Entry method: requestElevator
3- ELEVATOR CLASS
    3a- Constructor and its attributes
    3b- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    3c- Methods for logic: moveElevator, moveUp, moveDown, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, deleteFloorFromList
    3d- Entry method: requestFloor
4- DOOR CLASS
5- BUTTON CLASS
6- DISPLAY CLASS
7- ENUMS
8- TESTING PROGRAM

CONTROLLED OBJECTS:
Columns: controls a list of N elevators
Elevators: controls doors, buttons, displays

*************************************************** '''

from enum import Enum

''' ------------------------------------------- GLOBAL VARIABLES ---------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------- '''
numberOfColumns = 0
numberOfFloors = 0
numberOfElevators = 0
waitingTime = 0         #How many time the door remains opened in SECONDS
maxWeight = 0           #Maximum weight an elevator can carry in KG


''' ------------------------------------------- COLUMN CLASS ------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
class Column:
    ''' ------------------ Constructor and its attributes ------------------ '''
    def __init__(self, id, columnStatus, numberOfFloors, numberOfElevators):
        self.id = id
        self.status = columnStatus
        self.numberOfFloors = numberOfFloors
        self.numberOfElevators = numberOfElevators
        self.elevatorsList = []
        self.buttonsUpList = []
        self.buttonsDownList = []

        self.createElevatorsList()
        self.createButtonsUpList()
        self.createButtonsDownList()

    def display(self):
        print("Created column " + str(self.id))
        print("Number of floors: " + str(self.numberOfFloors))
        print("Created Number of elevators: " + str(self.numberOfElevators))
        print("----------------------------------")


    ''' ------------------ Methods to create a list ------------------ '''
    ''' CREATE A LIST OF ELEVATORS FOR THE COLUMN '''
    def createElevatorsList(self):
        for x in range(self.numberOfElevators):
            self.elevatorsList.append(Elevator(x + 1, self.numberOfFloors, 1, ElevatorStatus.IDLE, SensorStatus.OFF, SensorStatus.OFF))
            # print("elevator " + str(self.elevatorsList[x].id) + " created")

    ''' CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR '''
    def createButtonsUpList(self):
        for x in range(self.numberOfFloors - 1):
            self.buttonsUpList.append(Button(x + 1, ButtonStatus.OFF, x + 1))
            # print("button up " + str(self.buttonsUpList[x].id) + " created")

    ''' CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR '''
    def createButtonsDownList(self):
        for x in range(self.numberOfFloors - 1):
            self.buttonsDownList.append(Button(x + 2, ButtonStatus.OFF, x + 2))
            # print("button down " + str(self.buttonsDownList[x].id) + " created")


    ''' ------------------ Methods to create a logic ------------------ '''
    ''' LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC '''
    def findElevator(self, currentFloor, direction):
        activeElevatorList = []
        idleElevatorList = []
        sameDirectionElevatorList = []
        for x in (self.elevatorsList):
            if x.status != ElevatorStatus.IDLE:
                #verify if the request is on the elevator way
                if x.status == ElevatorStatus.UP and x.floor <= currentFloor or x.status == ElevatorStatus.DOWN and x.floor >= currentFloor:
                    activeElevatorList.append(x)
            else:
                idleElevatorList.append(x)
        
        if len(activeElevatorList) > 0: #Create new list for elevators with same direction that the request
            sameDirectionElevatorList = [elevator for elevator in activeElevatorList if elevator.status == direction]
        
        if len(sameDirectionElevatorList) > 0:
            bestElevator = self.findNearestElevator(currentFloor, sameDirectionElevatorList)
        else:
            bestElevator = self.findNearestElevator(currentFloor, idleElevatorList)
            
        return bestElevator

    ''' LOGIC TO FIND THE NEAREST ELEVATOR '''
    def findNearestElevator(self, currentFloor, selectedList):
        bestElevator = selectedList[0]
        bestDistance = abs(selectedList[0].floor - currentFloor) #abs() returns the absolute value of a number (always positive).
    
        for elevator in selectedList:
            if abs(elevator.floor - currentFloor) < bestDistance:
                bestElevator = elevator
        
        print()
        print("   >> >>> ELEVATOR " + str(bestElevator.id) + " WAS CALLED <<< <<")            
        return bestElevator


    ''' ------------------ Entry method ------------------ '''
    ''' ENTRY METHOD '''
    ''' REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR '''
    def requestElevator(self, requestedFloor, direction):
        if direction == ButtonDirection.UP:
            self.buttonsUpList[requestedFloor-1].status = ButtonStatus.ON
        else:
            self.buttonsDownList[requestedFloor-1].status = ButtonStatus.ON

        print(">> Someone request an elevator from floor <" + str(requestedFloor) + "> and direction <" + str(direction) + "> <<")
        for x in (self.elevatorsList):
            print("Elevator" + str(x.id) + " | " + "Floor: " + str(x.floor) + " | " + "Status: " + str(x.status.value))
       
        bestElevator = self.findElevator(requestedFloor, direction)
        # bestElevator.addFloorToFloorList(requestedFloor) #TODO
        # bestElevator.moveElevator(requestedFloor, self) #TODO


''' ------------------------------------------- ELEVATOR CLASS ----------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
class Elevator:
    def __init__(self, id, numberOfFloors, floor, elevatorStatus, weightSensorStatus, obstructionSensorStatus):
        self.id = id
        self.numberOfFloors = numberOfFloors
        self.floor = floor
        self.status = elevatorStatus
        self.weightSensor = weightSensorStatus
        self.obstructionSensor = obstructionSensorStatus
        self.elevatorDoor = Door(0, DoorStatus.CLOSED, 0)
        self.elevatorDisplay = Display(0, DisplayStatus.ON, 0)
        self.floorDoorsList = []
        self.floorDisplaysList = []
        self.floorButtonsList = []
        self.floorList = []

        self.createFloorDoorsList()   
        self.createDisplaysList()
        self.createFloorButtonsList()

    # To print the object:
    def __str__(self):
        return str(self.__class__) + ": " + str(self.__dict__)

    ''' CREATE A LIST WITH A DOOR OF EACH FLOOR '''
    def createFloorDoorsList(self):
        for x in range(self.numberOfFloors):
            self.floorDoorsList.append(Door(x + 1, DoorStatus.CLOSED, x + 1))
            # print("Elevator" + str(self.id) + " door " + str(self.floorDoorsList[x].id) + " created")

    ''' CREATE A LIST WITH A DISPLAY OF EACH FLOOR '''
    def createDisplaysList(self):
        for x in range(self.numberOfFloors):
            self.floorDisplaysList.append(Display(x + 1, DisplayStatus.ON, x + 1))
            # print("Elevator" + str(self.id) + " display " + str(self.floorDisplaysList[x].id) + " created")

    ''' CREATE A LIST WITH A BUTTON OF EACH FLOOR '''
    def createFloorButtonsList(self):
        for x in range(self.numberOfFloors):
            self.floorButtonsList.append(Button(x + 1, ButtonStatus.ON, x + 1))
            # print("Elevator" + str(self.id) + " button " + str(self.floorButtonsList[x].id) + " created")

    ''' LOGIC TO ADD A FLOOR TO THE FLOOR LIST ''' #TODO TEST THIS FUNCTION!!!
    def addFloorToFloorList(self, floor):
        self.floorList.append(floor)
        print("Elevator" + str(self.id) + " - floor " + str(floor) + " added to floorList")


''' ------------------------------------------- DOOR CLASS --------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
class Door:
    def __init__(self, id, doorStatus, floor):
        self.id = id
        self.status = doorStatus
        self.floor = floor


''' ------------------------------------------- BUTTON CLASS --------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
class Button:
    def __init__(self, id, buttonStatus, floor):
        self.id = id
        self.status = buttonStatus
        self.floor = floor


''' ------------------------------------------- DISPLAY CLASS --------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
class Display:
    def __init__(self, id, displayStatus, floor):
        self.id = id
        self.status = displayStatus
        self.floor = floor


''' ------------------------------------------- ENUMS -------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
''' ******* COLUMN STATUS ******* '''
class ColumnStatus(Enum):
    ACTIVE = 'active'
    INACTIVE = 'inactive'

''' ******* ELEVATOR STATUS ******* '''
class ElevatorStatus(Enum):
    IDLE = 'idle'
    UP = 'up'
    DOWN = 'down'

''' ******* BUTTON DIRECTION ******* '''
class ButtonDirection(Enum):
    UP = 'up'
    DOWN = 'down'

''' ******* BUTTON STATUS ******* '''
class ButtonStatus(Enum):
    ON = 'on'
    OFF = 'off'

''' ******* SENSOR STATUS ******* '''
class SensorStatus(Enum):
    ON = 'on'
    OFF = 'off'

''' ******* DOORS STATUS ******* '''
class DoorStatus(Enum):
    OPENED = 'opened'
    CLOSED = 'closed'

''' ******* DISPLAY STATUS ******* '''
class DisplayStatus(Enum):
    ON = 'on'
    OFF = 'off'


''' ------------------------------------------- TESTING PROGRAM -------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
waitingTime = 1 #How many time the door remains opened in SECONDS - I'm using 1 second so the test will run faster
maxWeight = 500 #Maximum weight an elevator can carry in KG

''' ******* CREATE SCENARIO 1 ******* '''
def scenario1(): 
    print()
    print("****************************** SCENARIO 1: ******************************")
    columnScenario1 = Column(1, ColumnStatus.ACTIVE, 10, 2) #parameters (id, columnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
    columnScenario1.display()  
    columnScenario1.elevatorsList[0].floor = 2 #floor where the elevator 1 is
    columnScenario1.elevatorsList[1].floor = 6 #floor where the elevator 2 is
    
    print()
    print("Person 1: (elevator 1 is expected)")
    columnScenario1.requestElevator(3, ButtonDirection.UP) #parameters (requestedFloor, buttonDirection.UP/DOWN)
    # columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1) #parameters (requestedFloor, requestedColumn)


    print("==================================")


''' ******* CALL SCENARIOS ******* '''
scenario1()