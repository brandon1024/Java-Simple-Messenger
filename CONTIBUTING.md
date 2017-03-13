# Contributing to Java-IRC

## Guidelines
I love to see people take an interest in a project I have been working on for years. I welcome anyone to help improve upon my work. Just get in touch with me!

##### Reporting an Issue
Did you notice an issue? I am sure I missed something. Let me know by [opening an issue](https://github.com/brandon1024/Java-IRC/issues). Be sure to check whether the issue has already been brought up (expand upon the issue if you have more details), or whether the issue has been fixed in a new release.

In creating a new issue, ensure you use a concise title, full description, and images if applicable. Also ensure you label the issue according to its type (UI, security, improvement, feature, etc).

##### Creating a Pull Request
Want to fix an issue yourself? Fork the repository, make changes, and then create a PR. I will do by best to check your changes in a timely manner.

## Code Style
I must admit, I am a little OCD (CDO in alphabetical order) when it comes to code style. I like the code to follow a certain style format. If the changes in your PR don't follow my code style, I might ask you to make some changes, or I may make the changes myself. To make my life easier in the future, here is a short style guide.

1. Use tabs instead of spaces.
2. Curly braces should appear on the next line.
3. An `if` statement with a single line body should not have curly braces.
4. A `for` or `while` loop with a single line body should not have curly braces.
5. Use `switch` statements sparingly
6. References to instance variables and methods should always use the `this` keywork.
7. If you create a new class, include a javadoc style comment before the class signature.

Example Code:
```
/**@author John Doe
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code ClassName} class ...*/

public class ClassName
{
     public ClassName()
     {
          if(true)
               this.doSomething();
          
          if(true)
          {
               this.doSomethingElse();
               this.doMoreStuff();
          }
          else
               this.doSomething();
               
          for(int i = 0; i < 10; i++)
               this.doSomething();
               
          for(int i = 0; i < 10; i++)
          {
               if(true)
                    this.doSomething();
          }
     }
     
     public void doSomething()
     {
          //Stuff
     }
     
     ...
}
```

## Current Contributors
- [Brandon Richardson](https://github.com/brandon1024)
  - Owner
