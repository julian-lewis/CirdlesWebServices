/*
 * Copyright 2017 CIRDLES.
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
package org.cirdles.webServices.ambapo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBException;
import org.cirdles.ambapo.ConversionFileHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author juliansmith
 */
public class AmbapoFileHandlerService {
    
    public AmbapoFileHandlerService()
    {
    }
    
    public Path convertFile(
            String infileName,
            InputStream ambapoFile,
            String conversionType) throws IOException, JAXBException, SAXException, Exception {

        Path uploadDirectory = Files.createTempDirectory("upload");
        Path ambapoFilePath = uploadDirectory.resolve("ambapo-file.csv");
        Files.copy(ambapoFile, ambapoFilePath);
        
        Path ambapoConversionFolderAlias = Files.createTempDirectory("conversion-destination");
        File outfileName = ambapoConversionFolderAlias.toFile();

        // this gives reportengine the name of the Prawnfile for use in report names
        ConversionFileHandler fileHandler = new ConversionFileHandler(infileName);
        
        if("UTMtoLatLong".equals(conversionType)){
            fileHandler.writeConversionsLatLongToUTM(outfileName);
        }
        
        else if("LatLongtoUTM".equals(conversionType)){
            fileHandler.writeConversionsUTMToLatLong(outfileName);
        }

        Files.delete(ambapoFilePath);
        
        Path outfile = outfileName.toPath();
        
        return outfile;
    }
}

