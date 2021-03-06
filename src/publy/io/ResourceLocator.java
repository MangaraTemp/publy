/*
 * Copyright 2013-2016 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.io;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import publy.Console;

/**
 *
 *
 */
public class ResourceLocator {

    private static final Path baseDirectory;

    static {
        Path workingDir;

        try {
            // Location of the publy package. Can be of one of two forms:
            // - path/build/classes - when run from within NetBeans
            // - path/*.jar - when run from a jar archive
            workingDir = Paths.get(ResourceLocator.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (workingDir.endsWith(Paths.get("build", "classes"))) {
                // Running in NetBeans, remove "build/classes"
                workingDir = workingDir.getParent().getParent();
            } else if (workingDir.getFileName().toString().endsWith(".jar")) {
                // Running from a jar
                workingDir = workingDir.getParent();
            } else {
                Console.warn(Console.WarningType.OTHER, "Working directory is of an unknown form:%n\"%s\"", workingDir.toString());
                workingDir = Paths.get(System.getProperty("user.dir"));
                Console.log("Reverted to working directory \"%s\".", workingDir.toString());
            }
        } catch (NullPointerException // From the long chain of initializers
                | SecurityException // Can be thrown from getProtectionDomain(), if a SecurityManager is enabled
                | URISyntaxException ex) {
            Console.except(ex, "Exception while initializing base directory:");
            workingDir = Paths.get(System.getProperty("user.dir"));
            Console.log("Reverted to working directory \"%s\".", workingDir.toString());
        }

        baseDirectory = workingDir;
    }

    /**
     * The directory that contains the JAR.
     *
     * @return
     */
    public static Path getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Returns a String representing the path to the given file that is relative
     * to the base directory. If this is not possible, the original path is cast
     * to a String and returned, unless the path is
     * <code>null</code>, in which case an empty String is returned instead.
     *
     * @param path
     * @return
     */
    public static String getRelativePath(Path path) {
        if (path == null) {
            return "";
        } else {
            try {
                return baseDirectory.relativize(path).toString();
            } catch (IllegalArgumentException ex) {
                return path.toString();
            }
        }
    }

    /**
     * Resolves the given path against the base directory. When the path is
     * <code>null</code> or empty, <code>null</code> is returned instead.
     *
     * @param relativePath
     * @return
     */
    public static Path getFullPath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        } else {
            return baseDirectory.resolve(relativePath);
        }
    }
}
