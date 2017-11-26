package bajtahack.database;

import java.util.logging.Logger;

/** Tale metoda zagotovi, da je logger na clasu vedno definiran z pravilim imenom razreda.
 * 
 * Kajti pri copy/pastu kode za logger se dostikrat zgodi, da je ime logerja na novem razredu od prejšnjega razreda.
 * 
 * Kličemo takole:
 * <code>
 * public static final Logger log = LoggingFactory.loggerForThisClass();
 * </code>
 * 
 * Tole pa so java.util.logging vs jboss mapping za logiranje
 * <pre> 
 * map.put(java.util.logging.Level.SEVERE, Level.ERROR);
 * map.put(java.util.logging.Level.WARNING, Level.WARN);
 * map.put(java.util.logging.Level.CONFIG, Level.DEBUG);
 * map.put(java.util.logging.Level.INFO, Level.INFO);
 * map.put(java.util.logging.Level.FINE, Level.DEBUG);
 * map.put(java.util.logging.Level.FINER, Level.DEBUG);
 * map.put(java.util.logging.Level.FINEST, Level.TRACE);
 * </pre>
 * 
 * 
 * @author mitjag
 *
 */
public class LoggingFactory {

    public static final String LOG_LINE_SEPARATOR = System.getProperty("line.separator");
    
    /** Gets the logger for caller class
     * @return
     */
    public static Logger loggerForThisClass() {
        
        // We use the third stack element; second is this method, first is .getStackTrace()
        
        StackTraceElement myCaller = Thread.currentThread().getStackTrace()[2];
        
        return Logger.getLogger(myCaller.getClassName());
    }
    
    
}
