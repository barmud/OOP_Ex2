# OOP_Ex2
# Ex2_1

In this section of the assigment we work with Threads,ThreadPoll,Regular case(only main Thread).

In each way of the above we will check how it collab with massive task as Files I/O , and calculate the time took to execute a task.

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



