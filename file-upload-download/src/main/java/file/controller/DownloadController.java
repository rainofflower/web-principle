package file.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import file.util.FileUtils;

/**
 * @author YangHui
 */
@RestController
@RequestMapping("download")
public class DownloadController {

    @RequestMapping(value = "0", method = {RequestMethod.GET, RequestMethod.POST})
    public void download0(@RequestParam String file, HttpServletResponse response){
        FileUtils.downloadFile0(file, response);
    }

    @RequestMapping(value = "1", method = {RequestMethod.GET, RequestMethod.POST})
    public void download1(@RequestParam String file, HttpServletResponse response){
        FileUtils.downloadFile1(new File(file), response);
    }

    @RequestMapping(value = "2", method = {RequestMethod.GET, RequestMethod.POST})
    public void download2(@RequestParam String file, HttpServletResponse response){
        FileUtils.downloadFile2(new File(file), response);
    }

    @RequestMapping(value = "3", method = {RequestMethod.GET, RequestMethod.POST})
    public void download3(@RequestParam String file, HttpServletResponse response){
        FileUtils.downloadFile3(new File(file), response);
    }

    @RequestMapping(value = "4", method = {RequestMethod.GET, RequestMethod.POST})
    public void download4(@RequestParam String file, HttpServletResponse response){
        FileUtils.downloadFile4(new File(file), response);
    }
}
