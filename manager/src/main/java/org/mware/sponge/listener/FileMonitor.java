package org.mware.sponge.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.io.File;

/**
 * Created by Dan on 7/11/2017.
 */
public class FileMonitor {
    private static final Logger log = LoggerFactory.getLogger(FileMonitor.class);

    private Timer timer_;
    private HashMap files_;
    private Collection listeners_;

    /**
     * @param pollingInterval
     */
    public FileMonitor (long pollingInterval) {
        files_     = new HashMap();
        listeners_ = new ArrayList();
        timer_ = new Timer (true);
        timer_.schedule (new FileMonitorNotifier(), 0, pollingInterval);
    }

    /**
     *
     */
    public void stop() {
        timer_.cancel();
    }

    /**
     * @param file
     */
    public void addFile (File file) {
        if (!files_.containsKey (file)) {
            long modifiedTime = file.exists() ? file.lastModified() : -1;
            files_.put (file, new Long (modifiedTime));
        }
    }

    /**
     * @param file
     */
    public void removeFile (File file) {
        files_.remove (file);
    }

    /**
     * @param fileListener
     */
    public void addListener (FileListener fileListener) {
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            FileListener listener = (FileListener) i.next();
            if (listener == fileListener) {
                return;
            }
        }

        listeners_.add (fileListener);
    }

    /**
     * @param fileListener
     */
    public void removeListener (FileListener fileListener) {
        for (Iterator i = listeners_.iterator(); i.hasNext(); ) {
            FileListener listener = (FileListener) i.next();
            if (listener == fileListener) {
                i.remove();
                break;
            }
        }
    }

    private class FileMonitorNotifier extends TimerTask {
        public void run() {
            Collection files = new ArrayList (files_.keySet());
            for (Iterator i = files.iterator(); i.hasNext(); ) {
                File file = (File) i.next();
                long lastModifiedTime = ((Long) files_.get (file)).longValue();
                long newModifiedTime  = insideFolderModifiedTime(file);
                // Chek if file has changed
                if (newModifiedTime != lastModifiedTime) {
                    // Register new modified time
                    files_.put (file, new Long (newModifiedTime));
                    // Notify listeners
                    for (Iterator j = listeners_.iterator(); j.hasNext(); ) {
                        FileListener listener = (FileListener) j.next();

                        log.info("File changed. Notify listener: "+listener);
                        if (listener == null) {
                            j.remove();
                        } else {
                            listener.fileChanged (file);
                        }
                    }
                }
            }
        }

        private long insideFolderModifiedTime(File file) {
            long _time = file.exists() ? file.lastModified() : -1;
            if (file.isDirectory()) {
                for (String subFile : file.list()) {
                    long _subTime = insideFolderModifiedTime(new File(file, subFile));

                    if (_subTime > _time) {
                        _time = _subTime;
                    }
                }
            }

            return _time;
        }
    }
}
