
''' ***************************************************
	@Author			Cindy Okino
	@Website		https://github.com/cindyokino
	@Last Update	October 2, 2020
    
    
SUMMARY:
1- GLOBAL VARIABLES
2- COLUMN CLASS
3- ELEVATOR CLASS
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

    def createElevatorsList(self):
        for x in range(self.numberOfElevators):
            self.elevatorsList.append(Elevator(x + 1, self.numberOfFloors, 1, ElevatorStatus.IDLE, SensorStatus.OFF, SensorStatus.OFF))
            # print("elevator " + str(self.elevatorsList[x].id) + " created")

    def createButtonsUpList(self):
        for x in range(self.numberOfFloors - 1):
            self.buttonsUpList.append(Button(x + 1, ButtonStatus.OFF, x))
            # print("button up " + str(self.buttonsUpList[x].id) + " created")

    def createButtonsDownList(self):
        for x in range(self.numberOfFloors - 1):
            self.buttonsDownList.append(Button(x + 2, ButtonStatus.OFF, x))
            # print("button down " + str(self.buttonsDownList[x].id) + " created")



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

        # self.createFloorDoorsList()   
        # self.createDisplaysList()
        # self.createFloorButtonsList()

    # To print the object
    def __str__(self):
        return str(self.__class__) + ": " + str(self.__dict__)

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
    columnScenario1.elevatorsList[0].floor = 2 #floor where the elevator 1 is
    columnScenario1.elevatorsList[1].floor = 6 #floor where the elevator 2 is
    
    # columnScenario1.requestElevator(3, ButtonDirection.UP); #parameters (requestedFloor, buttonDirection.UP/DOWN)
    # columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1); #parameters (requestedFloor, requestedColumn)

    columnScenario1.display()  

    print("==================================")


''' ******* CALL SCENARIOS ******* '''
scenario1()