''' ***************************************************
	@Author			Cindy Okino
	@Website		https://github.com/cindyokino
	@Last Update	October 2, 2020
    
    
SUMMARY:
0- IMPORTS
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
9- TEST YOUR SCENARIO

CONTROLLED OBJECTS:
Columns: controls a list of N elevators
Elevators: controls doors, buttons, displays

*************************************************** '''

''' ------------------------------------------- IMPORTS ------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------- '''
from enum import Enum
import math
import random
import time


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


    ''' ------------------ Methods for logic ------------------ '''
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
            self.buttonsDownList[requestedFloor-2].status = ButtonStatus.ON

        print(">> Someone request an elevator from floor <" + str(requestedFloor) + "> and direction <" + str(direction.value) + "> <<")
        for x in (self.elevatorsList):
            print("Elevator" + str(x.id) + " | " + "Floor: " + str(x.floor) + " | " + "Status: " + str(x.status.value))
       
        bestElevator = self.findElevator(requestedFloor, direction)
        bestElevator.addFloorToFloorList(requestedFloor) 
        bestElevator.moveElevator(requestedFloor, self)


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


    ''' ------------------ Methods to create a list ------------------ '''
    ''' CREATE A LIST WITH A DOOR OF EACH FLOOR '''
    def createFloorDoorsList(self):
        for x in range(self.numberOfFloors):
            self.floorDoorsList.append(Door(x + 1, DoorStatus.CLOSED, x + 1))

    ''' CREATE A LIST WITH A DISPLAY OF EACH FLOOR '''
    def createDisplaysList(self):
        for x in range(self.numberOfFloors):
            self.floorDisplaysList.append(Display(x + 1, DisplayStatus.ON, x + 1))

    ''' CREATE A LIST WITH A BUTTON OF EACH FLOOR '''
    def createFloorButtonsList(self):
        for x in range(self.numberOfFloors):
            self.floorButtonsList.append(Button(x + 1, ButtonStatus.ON, x + 1))


    ''' ------------------ Methods for logic ------------------ '''
    ''' LOGIC TO MOVE ELEVATOR '''
    def moveElevator(self, requestedFloor, requestedColumn):
        while len(self.floorList) != 0:
            if self.status == ElevatorStatus.IDLE:
                if self.floor < requestedFloor:
                     self.status = ElevatorStatus.UP
                elif self.floor == requestedFloor:
                    self.openDoors(waitingTime)
                    self.deleteFloorFromList(requestedFloor) 
                    requestedColumn.buttonsUpList[requestedFloor-1].status = ButtonStatus.OFF
                    requestedColumn.buttonsDownList[requestedFloor-1].status = ButtonStatus.OFF
                    self.floorButtonsList[requestedFloor-1].status = ButtonStatus.OFF
                else:
                    self.status = ElevatorStatus.DOWN

            if self.status == ElevatorStatus.UP:
                self.moveUp(requestedColumn)
            else:
                 self.moveDown(requestedColumn)                 

    ''' LOGIC TO MOVE UP '''
    def moveUp(self, requestedColumn):
        tempArray = self.floorList.copy()
        for x in range(self.floor, tempArray[len(tempArray) - 1]):
            if self.floorDoorsList[x].status == DoorStatus.OPENED or self.elevatorDoor.status == DoorStatus.OPENED:
                print("   Doors are open, closing doors before move up")
                self.closeDoors()

            print("Moving elevator" + str(self.id) + " <up> from floor " + str(x) + " to floor " + str(x + 1)) 
            nextFloor = (x + 1)
            self.floor = nextFloor
            self.updateDisplays(self.floor)

            if nextFloor in tempArray:
                self.openDoors(waitingTime)
                self.deleteFloorFromList(nextFloor)
                requestedColumn.buttonsUpList[x - 1].status = ButtonStatus.OFF
                self.floorButtonsList[x].status = ButtonStatus.OFF
        
        if len(self.floorList) == 0:
            self.status = ElevatorStatus.IDLE
            # print("       Elevator"+ str(self.id) + " is now " + str(self.status.value))
        else:
            self.status = ElevatorStatus.DOWN
            print("       Elevator"+ str(self.id) + " is now going " + str(self.status.value))

    ''' LOGIC TO MOVE DOWN '''
    def moveDown(self, requestedColumn):
        tempArray = self.floorList.copy()
        for x in range(self.floor, (tempArray[len(tempArray) - 1]), -1):
            if self.floorDoorsList[x - 1].status == DoorStatus.OPENED or self.elevatorDoor.status == DoorStatus.OPENED:
                print("   Doors are open, closing doors before move down")
                self.closeDoors()

            print("Moving elevator" + str(self.id) + " <down> from floor " + str(x) + " to floor " + str(x - 1)) 
            nextFloor = (x - 1)
            self.floor = nextFloor
            self.updateDisplays(self.floor)

            if nextFloor in tempArray:
                self.openDoors(waitingTime)
                self.deleteFloorFromList(nextFloor)
                requestedColumn.buttonsDownList[x - 2].status = ButtonStatus.OFF
                self.floorButtonsList[x - 1].status = ButtonStatus.OFF
        
        if len(self.floorList) == 0:
            self.status = ElevatorStatus.IDLE
            # print("       Elevator"+ str(self.id) + " is now " + str(self.status.value))
        else:
            self.status = ElevatorStatus.UP
            print("       Elevator"+ str(self.id) + " is now going " + str(self.status.value))

    ''' LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR '''
    def updateDisplays(self, elevatorFloor):
        for display in self.floorDisplaysList:
            display.floor = elevatorFloor
            
        print("Displays show #" + str(elevatorFloor))

    ''' LOGIC TO OPEN DOORS '''
    def openDoors(self, waitingTime):
        print("       Opening doors...")
        print("       Elevator" + str(self.id) + " doors are opened")
        time.sleep(waitingTime)
        self.elevatorDoor.status.OPENED
        self.floorDoorsList[self.floor-1].status = DoorStatus.OPENED
        self.closeDoors()

    ''' LOGIC TO CLOSE DOORS '''
    def closeDoors(self):
        if self.weightSensor == SensorStatus.OFF and self.obstructionSensor == SensorStatus.OFF:
            print("       Closing doors...")
            print("       Elevator" + str(self.id) + " doors are closed")
            self.floorDoorsList[self.floor-1].status = DoorStatus.CLOSED 

    ''' LOGIC FOR WEIGHT SENSOR '''
    def checkWeight(self, maxWeight):
        weight = math.floor((random.random() * 600) + 1) #This random simulates the weight from a weight sensor
        while weight > maxWeight:
            self.weightSensor = SensorStatus.ON
            print("       ! Elevator capacity reached, waiting until the weight is lower before continue...")
            weight -= 100 #I'm supposing the random number is 600, I'll subtract 100 so it will be less than 500 (the max weight I proposed) for the second time it runs
        
        self.weightSensor = SensorStatus.OFF
        print("       Elevator capacity is OK")

    ''' LOGIC FOR OBSTRUCTION SENSOR '''
    def checkObstruction(self):
        probabilityNotBlocked = 70
        number = math.floor((random.random() * 100) + 1) #This random simulates the probability of an obstruction (I supposed 30% of chance something is blocking the door)

        while number > probabilityNotBlocked:
            self.obstructionSensor = SensorStatus.ON
            print("       ! Elevator door is blocked by something, waiting until door is free before continue...")
            number -= 30  #I'm supposing the random number is 100, I'll subtract 30 so it will be less than 70 (30% probability), so the second time it runs theres no one blocking the door

        self.obstructionSensor = SensorStatus.OFF
        print("       Elevator door is FREE")

    ''' LOGIC TO ADD A FLOOR TO THE FLOOR LIST ''' 
    def addFloorToFloorList(self, floor):
        self.floorList.append(floor)
        print("Elevator" + str(self.id) + " - floor " + str(floor) + " added to floorList")

    ''' LOGIC TO DELETE ITEM FROM FLOORS LIST '''
    def deleteFloorFromList(self, stopFloor):
        index = self.floorList.index(stopFloor)
        if index > -1:
            self.floorList.pop(index)


    ''' ------------------ Entry method ------------------ '''
    ''' ENTRY METHOD '''
    ''' REQUEST FOR A FLOOR BY PRESSING THE FLOOR BUTTON INSIDE THE ELEVATOR '''
    def requestFloor(self, requestedFloor, requestedColumn):
        print()
        print(">> Someone inside the elevator" + str(self.id) + " wants to go to floor <" + str(requestedFloor) + "> <<")
        self.checkWeight(maxWeight)
        self.checkObstruction()
        self.addFloorToFloorList(requestedFloor)
        self.moveElevator(requestedFloor, requestedColumn)


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


''' ------------------------------------------- DISPLAY CLASS -----------------------------------------------------------------------
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


''' ------------------------------------------- TESTING PROGRAM ---------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
waitingTime = 1 #How many time the door remains opened in SECONDS - I'm using 1 second so the test will run faster
maxWeight = 500 #Maximum weight an elevator can carry in KG

''' ******* CREATE SCENARIO 1 ******* '''
def scenario1(): 
    print()
    print("****************************** SCENARIO 1: ******************************")
    columnScenario1 = Column(1, ColumnStatus.ACTIVE, 10, 2) #parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
    columnScenario1.display()  
    columnScenario1.elevatorsList[0].floor = 2 #floor where the elevator 1 is
    columnScenario1.elevatorsList[1].floor = 6 #floor where the elevator 2 is
    
    print()
    print("Person 1: (elevator 1 is expected)")
    columnScenario1.requestElevator(3, ButtonDirection.UP) #parameters (requestedFloor, buttonDirection.UP/DOWN)
    columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1) #parameters (requestedFloor, requestedColumn)
    print("==================================")

''' ******* CREATE SCENARIO 2 ******* '''
def scenario2(): 
    print()
    print("****************************** SCENARIO 2: ******************************")
    columnScenario2 = Column(1, ColumnStatus.ACTIVE, 10, 2)
    columnScenario2.display()  
    columnScenario2.elevatorsList[0].floor = 10
    columnScenario2.elevatorsList[1].floor = 3
    
    print()
    print("Person 1: (elevator 2 is expected)")
    columnScenario2.requestElevator(1, ButtonDirection.UP)
    columnScenario2.elevatorsList[1].requestFloor(6, columnScenario2)
    print("----------------------------------")
    print()
    print("Person 2: (elevator 2 is expected)")
    columnScenario2.requestElevator(3, ButtonDirection.UP)
    columnScenario2.elevatorsList[1].requestFloor(5, columnScenario2)
    print("----------------------------------")
    print()
    print("Person 3: (elevator 1 is expected)")
    columnScenario2.requestElevator(9, ButtonDirection.DOWN)
    columnScenario2.elevatorsList[0].requestFloor(2, columnScenario2)
    print("==================================")

''' ******* CREATE SCENARIO 3 ******* '''
def scenario3(): 
    print()
    print("****************************** SCENARIO 3: ******************************")
    columnScenario3 = Column(1, ColumnStatus.ACTIVE, 10, 2)
    columnScenario3.display()  
    columnScenario3.elevatorsList[0].floor = 10
    columnScenario3.elevatorsList[1].floor = 3
    columnScenario3.elevatorsList[1].status = ElevatorStatus.UP

    
    print()
    print("Person 1: (elevator 1 is expected)")
    columnScenario3.requestElevator(3, ButtonDirection.DOWN)
    columnScenario3.elevatorsList[0].requestFloor(2, columnScenario3)
    print("----------------------------------")    
    print()

    # 2 minutes later elevator 1(B) finished its trip to 6th floor
    columnScenario3.elevatorsList[1].floor = 6
    columnScenario3.elevatorsList[1].status = ElevatorStatus.IDLE

    print("Person 2: (elevator 2 is expected)")
    columnScenario3.requestElevator(10, ButtonDirection.DOWN)
    columnScenario3.elevatorsList[1].requestFloor(3, columnScenario3)
    print("==================================")


''' -------- CALL SCENARIOS -------- '''
scenario1()
# scenario2()
# scenario3()


''' ---------------------------------------------------------------------------------------------------------------------------------
------------------------------------------- TEST YOUR SCENARIO ----------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------- '''
# Instruction for your test: 
# 1- Uncomment the scenarioX() function
# 2- Change the 'X' for a value (see the notes to fill correctly at the comments at right of each line)
# 3- Uncomment the 'scenarioX()' at the last line of code
# 4- Run the code using a terminal of your preference by typing: Residential_Controller.py 

# def scenarioX():  
#     print()
#     print("****************************** SCENARIO X: ******************************")
#     columnX = Column(X, ColumnStatus.X, X, X) #set parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
#     columnX.display()  
#     columnX.elevatorsList[0].floor = X #floor where the elevator 1 is
#     columnX.elevatorsList[1].floor = X #floor where the elevator 2 is
#     # If you have more than 2 elevators, make a copy of the line above and put the corresponding index inside the brackets [X]

#     print()
#     print("Person x: (elevator x is expected)")
#     columnX.requestElevator(X, ButtonDirection.X) #set parameters (requestedFloor, buttonDirection.UP/DOWN)
#     columnX.elevatorsList[X].requestFloor(X, columnX) # choose elevator by index and set parameters (requestedFloor, requestedColumn)
#     print("==================================")

# scenarioX()