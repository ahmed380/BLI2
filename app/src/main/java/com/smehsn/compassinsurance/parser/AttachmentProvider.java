package com.smehsn.compassinsurance.parser;

import java.io.File;
import java.util.List;

/**
 * @author Sam
 */
public interface AttachmentProvider {
    List<File> getAttachedFiles();
}
