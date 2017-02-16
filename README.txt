In this project I have implemented the code for Counting the frequency of all the Alpha-numerics 
in "Pride and Prejudice" and then printed the 25 most occuring words. The catch here is that every 
program has been written under various constraints. This is inspired from the book "Exercises in 
Programming Style" written by Prof. Cristina Videira Lopes; which I have referred extensively 
for this project. All the implementation has been done in Java.

Currently uploaded styles.

Style1 - Good Old Times
In this style you can't create identifiers except for FileReaders and FileWriters, and 
the RAM memory is very expensive; hence we try to load as less items as possible 
in the memory. 

Style2 - Go Forth
In this style, we are trying to emulate the Forth Programming Style. Where there is 
a data stack. All operations (conditionals, arithmetic, etc.) are done over data on the 
stack. There exists a heap for storing data that’s needed for later operations. The heap 
data can be associated with names (i.e. variables). As said above, all operations are 
done over data on the stack, so any heap data that needs to be operated upon needs 
to be moved first to the stack and eventually back to the heap. Abstraction is allowed 
in the form of user-defined “procedures” (i.e. names bound to a set of instructions), 
but data transfer between procedures need to happen via Stack ONLY. Heap is just to 
store intermediate values.

Style3 - Monolithic
In this style, we are allowed the use of variables, but we aren't allowed to create 
abstractions or procedures. The use of libraries need to be as minimal as possible. 

Style4 - CookBook
In this programming style, all the procedures can now interact with the global variables, 
and can edit them as per their own implementation. The functions in this case aren't 
idempotent.

Style5 - Pipeline
In this programming style, all the procedures are in a pipeline, where the output produced 
by one procedure is consumed by another. I have implemented this in 2 variations.
Pipeline1.java - The traditional implementation where one block is processed completely 
						and then passed on to the next function
Pipeline2.java - Using Streams and concurrency, where as soon an element is processed 
						by one function, it is passed forward to the next function. This ensures 
						that multiple functions are working concurrently on a different chunk 
						in the same set of blocks; improving the performance speed at times.