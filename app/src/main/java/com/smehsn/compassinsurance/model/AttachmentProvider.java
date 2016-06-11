package com.smehsn.compassinsurance.model;

import android.net.Uri;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface AttachmentProvider {
    public List<File> getAttachedFiles();
}
