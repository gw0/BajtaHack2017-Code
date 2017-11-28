#ifndef LIBSERIAL_H
#define LIBSERIAL_H


// Used for TimeOut operations
//#include <sys/time.h>
// Include for windows
#if defined (_WIN32) || defined( _WIN64)
    // Accessing to the serial port under Windows
    #include <windows.h>
    #include <time.h>
#endif

// Include for Linux
#ifdef __linux__
	#include <sys/time.h>
    #include <stdlib.h>
    #include <sys/types.h>
    #include <sys/shm.h>
    #include <termios.h>
    #include <string.h>
    #include <iostream>
    // File control definitions
    #include <fcntl.h>
    #include <unistd.h>
    #include <sys/ioctl.h>
#endif


class libserial{
public:
    // Constructor of the class
    libserial    ();
    // Destructor
    ~libserial   ();



    //_________________________________________
    // ::: Configuration and initialization :::
    // Open a device
    char    Open        (const char *Device,const unsigned int Bauds);
    // Close the current device
    void    Close();



    //___________________________________________
    // ::: Read/Write operation on characters :::
    // Write a char
    char    WriteChar   (char);
    // Read a char (with timeout)
    char    ReadChar    (char *pByte,const unsigned int TimeOut_ms=0);


    //________________________________________
    // ::: Read/Write operation on strings :::
    // Write a string
    char    WriteString (const char *String);
    // Read a string (with timeout)
    int     ReadString  (char *String, char FinalChar, unsigned int MaxNbBytes, const unsigned int TimeOut_ms=0);


    // _____________________________________
    // ::: Read/Write operation on bytes :::
    // Write an array of bytes
    char    Write       (const void *Buffer, const unsigned int NbBytes);
    // Read an array of byte (with timeout)
    int     Read        (void *Buffer,unsigned int MaxNbBytes,const unsigned int TimeOut_ms=0);


    // _________________________
    // ::: Special operation :::
    // Empty the received buffer
    void    FlushReceiver();
    // Return the number of bytes in the received buffer
    int     Peek();

private:
    // Read a string (no timeout)
    int     ReadStringNoTimeOut  (char *String,char FinalChar,unsigned int MaxNbBytes);


#if defined (_WIN32) || defined( _WIN64)
    HANDLE          hSerial;
    COMMTIMEOUTS    timeouts;
#endif
#ifdef __linux__
    int             fd;
#endif

};



/*!  \class     TimeOut
     \brief     This class can manage a timer which is used as a timeout.
   */
// Class TimeOut
class TimeOut
{
public:

    // Constructor
    TimeOut();

    // Init the timer
    void                InitTimer();

    // Return the elapsed time since initialization
    unsigned long int   ElapsedTime_ms();

private:    
    struct timeval      PreviousTime;
};


#endif // LIBSERIAL_H
