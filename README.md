# OOP_Ex2
# Ex2_1

In this section of the assigment we work with Threads,ThreadPoll,Regular case(only main Thread).

In each way of the above we will check how it collab with massive task as Files I/O , and calculate the time took to execute the task.

## Working with files
Function ``createTextFiles(int n,int seed,int bound)`` will generate n files which will be filled with random num of lines. the line : "Hello World". This function return String array of files name.

### Regular case:
Function : ``getNumOfLines(String[] fileNames)``

In this function using only the main Thread , we go through each file one by one and add his num of lines to a total counter.

### Thread case:
Function: ``getNumOfLinesThreads(String[] fileNames)``

In this function we read line numbers by using a thread for each file we read the files simultaneously.

In order to do that we create Inner class 'LinesThread' which have a variable of ``lines`` that maintain for each file the amount of lines.

Each file will get a thread by jvm , the thread will update the amount of ``lines`` and after he finish we will add the amount of lines he read to total counter.

### ThreadPool case:
Function :``getNumOfLinesThreadPool(String[] fileNames)``

In this function we create threadPool using ExecutorService which responsible for creating threads that fullfil our task - reading lines from files.

In this function we read line numbers by injecting for each thread an synchronous task - ``callable`` type - this task will return us the amount of lines in each file.

Using ``SafeCounter`` an atomic integer we can insure the threads will be synchronized bettwen themselves who change the line counter , and only one thread at a time.
By using ``setValue`` of ``SafeCounter`` we add the amount of each file lines to the total.

## Time Conclusions:
For each case we check times and compare one case to another.

By overall looking we can see ThreadPool is winning , than Threads case and at last is the regular case.

*note*: we use around 1000 files and 500 lines in average to liken to heavy tasks.

This photo will reinforce our findings:

![WhatsApp Image 2023-01-11 at 19 16 55](https://user-images.githubusercontent.com/118991774/211874454-61e3e7ec-0c30-44d5-8907-061ef39088d2.jpeg)

This result is match our expectations for those reasons : 
- ThreadsPool and Threads case both using multiple execution paths and therfore there are much faster than regular case
- ThreadsPool is faster than regular threads beacuse regular thread's life span shoter in compare to ThreadsPool. 
ThreadPool is reusing threads that have already been created instead of creating new ones as made at regular threads case , which is expensive process.
- threadpools often have mechanisms in place to manage and schedule the execution of tasks, which can also contribute to increased performance.


### Hierarchical diagram :
##
![WhatsApp Image 2023-01-11 at 18 56 49](https://user-images.githubusercontent.com/118991774/212070364-54b0b494-4241-4f3b-bf2f-f82b9a3d5f9d.jpeg)


# **************************************************

# Ex2_2
Part2 of the assigment.

In this part we will create our own ThreadPoolExecutor , which will bring to the ThreadPool new attribute:

- The ability to priority mission.

This will happen by a new kind of task that we create which contain asynchronous task - callable and priority Enum for the task.

Our ThreadPoolExecutor will determine the order of tasks to execute according priority number of each task.

# Classes:

## Task

### Overall:
Class that will maintain the idea of ``Callable`` of asynchronous task with generic return value , and will hold for each task his priority value.
##

This class extends ``FutureTask<V>`` and implements ``Callable<V>, Comparable<Task<V>>``

- We are implements from ``Callable`` interface cause we want to implement certain design 
 principles in our class - That our ``Task`` will be parrell to ``Callable`` as 
 asynchronous task with generic return value but also refurbish with a new attribute of 
 prriority Enum for each task.

- We are use ``Comparable`` Interface to reuese certain behaviors in our code as adjusting the functinon ``CompareTo()`` to compare by value bettwen two ``Task`` objects.

- We extends FutureTask to inherit all of the functionality provided by FutureTask for managing the execution and result of an asynchronous task. 
## Paramters
- Callable<V>: This is the ``Callable`` object that is being executed by the task. It represents the task that will be executed and it's of generic type V representing the type of the result that the task will produce.
- TaskType: This is an enumeration that represents the priority of the task. The lower the value the more important the task.

### Desgin patterns:
#### Factory desgin pattern:
The purpose of this desgin pattern is allow to a creation of objects to be encapsulated within a factory, hiding the implementation details and making it easier to change the way objects are created without affecting the rest of the code.This will help the programmer that will use the class to avoid from knowing the complex API of the code.

In our code :

**Factory** :

- ![image](https://user-images.githubusercontent.com/118991774/212047998-968b5d1d-836f-4a20-85f3-b9b77cc6411c.png)
- ![image](https://user-images.githubusercontent.com/118991774/212048187-eb29c26d-2255-4e77-a43c-bc150170e697.png)

***note**: they are both public and static in order to use outside the class without needed object of the class. ``(Task.createTask(...))``*


**That hide**: 
- ![image](https://user-images.githubusercontent.com/118991774/212048423-2bdaa3e6-c2f4-453f-be71-bdbd1be375e2.png)

***note**: the constructor is private in order that only the factory could create new ``Task``.*

#### Solid Principles:

Single Responsibility Principle: By using factory pattern, you can separate the responsibility of creating an object from the class that uses it, which follows the Single Responsibility Principle (SRP) of SOLID principles.

## CustomExecutor
### Overall:
##
Class that will refurbish ``ThreadPool`` by changeing his regular blocking queue to priority blocking queue and use ``Task`` in his queue instead of ``Callable`` or ``Runnable``. 

This will allow to prioritize missions to accomplish.

### Class parameters:
##
- ``ThreadPoolExecutor threadpool``:  The ThreadPool that we will refurbish.
- ``int corePoolSize``: The minimum amount of worker threads that can be used to execute tasks in parallel.
- ``int maxPoolSize``: The maximum amount of worker threads that can be used to execute tasks in parallel.
- ``int currentMax``: The priority num of the most important task at the moment.
- ``int[] priorityArray``: This is array counter , each cell index represent the priority number and the value represnt how many task at the moment with priority number of the index wait for execute.

### Methods
##
- ``submitTask(Task<V> task)``: A factory method that takes a Task object, add the task priority to the priorityArray, and submits it to the thread pool for execution.
- ``submit(Task task)``: Allows to submit a new task to the thread-pool using the factory method.
- ``submit(Callable c,TaskType priority)``: allows to initialize a new task from a given Callable object and priority and submits it to the thread-pool using the factory method.
- ``gracefullyTerminate()``: shut down the thread-pool and wait for all the threads to finish their work.

### How to determine ``currentMax``
##
We want to get the current Maximum priority in O(1) without approach the queue of ThreadPool. 

Therefore , when a new task execute we enter her num of priorrity to the priorityArray - happened at ``submitTask(Task<V> task)``.

And we remove a task from the priorityArray through ``beforeExecute()`` function , this function allowed us to approach the Thread that assigned to execute the task , after he remove the task from the queue and still waiting before using ``call()`` to calculate her.

***note** : the beforeExecute method assumes that the priority queue is correctly updated with the correct priority values of the tasks in the queue due to ``compareTo()``.*

### Hierarchical diagram :
##
 ![WhatsApp Image 2023-01-11 at 18 56 04](https://user-images.githubusercontent.com/118991774/212070185-12e89398-82d9-4f87-bdc0-1b201238559d.jpeg)







