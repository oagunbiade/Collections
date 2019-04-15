package com.coronation.collections.contollers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.coronation.collections.domain.Payment;
import com.coronation.collections.domain.User;
import com.coronation.collections.services.ExcelFileGenerator;
import com.coronation.collections.services.PaymentService;
import com.coronation.collections.services.UserService;

@Controller
public class FileController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private PaymentService paymentService;
	

	@Value("${upload.path}")
    private String path;
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@RequestMapping(value = "/payment/{userId}/doUpload", method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    public String upload(@PathVariable("userId") Long userId,@RequestParam MultipartFile file) throws IOException {

		ExcelFileGenerator excelGen = new ExcelFileGenerator();
		User user = this.userService.findById(userId);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssss");
				 
		logger.info("User " +user.getUserName());
		
		logger.info("inside file controller " +file);
		
		logger.info("inside uploadfile method " +file);
		
		String  fileName = file.getOriginalFilename();

		logger.info("File name is " +fileName);
        InputStream is = file.getInputStream();

        try {
			Files.copy(is, Paths.get(path + fileName),
			        StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		excelGen.setFileName(path + file.getOriginalFilename());
		List<List<String>> list = excelGen.readExcel();
		logger.info("upload list.size()==="+list.size());
		
		List <Payment> paymentList = new ArrayList<Payment>();
		
		
		 int i = 1;
		 for (List<String> list2 : list) {
             if(!list2.isEmpty()) {
             Payment payment = new Payment();
             payment.setReferenceCode(sdf.format(timestamp));
             paymentList.add(payment);
             i++;
             }
       }
		 paymentService.saveMultiPayments(paymentList);


        return "redirect:/user/"+userId;
    }

    @ExceptionHandler(StorageException.class)
    public String handleStorageFileNotFound(@PathVariable("userId") Long userId,StorageException e) {

    	return "redirect:/user/"+userId;
    }

}
