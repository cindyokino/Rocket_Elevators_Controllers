
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
        #self.buttonsUpList()
        #self.buttonsDownList()

    '''def display(self):
        print("Created column" + str(self.id))
        print("Number of floors:" + str(self.numberOfFloors))
        print("Created Number of elevators:" + str(self.numberOfElevators))
        print("----------------------------------")'''
        
    print("Created column " + str(id))
    print("Number of floors: " + str(numberOfFloors))
    print("Created Number of elevators: " + str(numberOfElevators))
    print("----------------------------------")

    def createElevatorsList(self):
        for x in range(self.numberOfElevators):
            self.elevatorsList.insert(elevator = Elevator(x, self.numberOfFloors, 1, ElevatorStatus.IDLE, SensorStatus.OFF, SensorStatus.OFF))



# col = Column(1, 2,3,4)
# col.display()


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
