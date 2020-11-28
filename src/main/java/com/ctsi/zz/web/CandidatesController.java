package com.ctsi.zz.web;

import com.alibaba.excel.EasyExcel;
import com.ctsi.ssdc.criteria.StringCriteria;
import com.ctsi.ssdc.model.PageResult;
import com.ctsi.ssdc.util.HeaderUtil;
import com.ctsi.ssdc.util.ResponseUtil;
import com.ctsi.zz.domain.CandidateData;
import com.ctsi.zz.domain.Candidates;
import com.ctsi.zz.domain.CandidatesExample;
import com.ctsi.zz.listener.CandidatesExcelListener;
import com.ctsi.zz.service.CandidatesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


/**
 * REST controller for managing Candidates.
 *
 * @author ctsi-biyi-generator
 *
 */
@RestController
@RequestMapping("/api")
public class CandidatesController {

    private final Logger log = LoggerFactory.getLogger(CandidatesController.class);

    private static final String ENTITY_NAME = "candidates";

    private final CandidatesService candidatesService;

    public CandidatesController(CandidatesService candidatesService) {
        this.candidatesService = candidatesService;
    }

    @InitBinder   
    public void initBinder(WebDataBinder binder) {   
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   
        dateFormat.setLenient(true);   
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   
    } 
   
    /**
     * POST  /candidatess : Create a new candidates.
     *
     * @param candidates the candidates to create
     * @return the ResponseEntity with status 201 (Created) and with body the new candidates, or with status 400 (Bad Request) if the candidates has already an phone
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/candidatess")
    public ResponseEntity<Candidates> createCandidates(@RequestBody Candidates candidates) throws URISyntaxException {
        log.debug("REST request to save Candidates : {}", candidates);
        Candidates result = candidatesService.insert(candidates);
        return ResponseEntity.created(new URI("/api/candidatess" + "/" +result.getPhone() ))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getPhone().toString()))
            .body(result);
    }
	
    /**
     * PUT  /candidatess : Updates an existing candidates.
     *
     * @param candidates the candidates to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated candidates,
     * or with status 400 (Bad Request) if the candidates is not valid,
     * or with status 500 (Internal Server Error) if the candidates couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/candidatess")
    public ResponseEntity<Candidates> updateCandidates(@RequestBody Candidates candidates)  {
        log.debug("REST request to update Candidates : {}", candidates);
        Candidates result = candidatesService.update(candidates);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getPhone().toString()))
            .body(result);
    }
    
    /**
     * GET  /candidatess : get the candidatess with page.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of candidatess in body
     */
    @GetMapping("/candidatessByCriteria")
    public PageResult<Candidates> getCandidatessByCriteria(CandidatesExample candidatesExample, Pageable page) {
        log.debug("REST request to get CandidatessByCriteria");
        return candidatesService.findByExample(candidatesExample, page);
    }
    
    /**
     * GET  /candidatess : get the candidatess.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of candidatess in body
     */
    @GetMapping("/candidatessList")
    public PageResult<Candidates> getCandidatessList(CandidatesExample candidatesExample) {
        log.debug("REST request to get CandidatessList");
        return candidatesService.findByExample(candidatesExample);
    }

    /**
     * GET  /candidatess/:phone : get the "phone" candidates.
     *
     * @param phone the id of the candidates to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the candidates, or with status 404 (Not Found)
     */
    @GetMapping("/candidatess/{phone}")
    public ResponseEntity<Candidates> getCandidates(@PathVariable String phone) {
        log.debug("REST request to get Candidates : {}", phone);
        Candidates candidates = candidatesService.findOne(phone);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(candidates));
    }
	
    /**
     * DELETE  /candidatess/:phone : delete the "phone" candidates.
     *
     * @param phone the id of the candidates to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/candidatess/{phone}")
    public ResponseEntity<Void> deleteCandidates(@PathVariable String phone) {
        log.debug("REST request to delete Candidates : {}", phone);
        candidatesService.delete(phone);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, phone.toString())).build();
    }

    @GetMapping("/qyeryByName/{name}")
    public PageResult<Candidates> queryByName(@PathVariable String name) {
        log.debug("REST request to get Candidates : {}", name);
        CandidatesExample candidatesExample = new CandidatesExample();
        StringCriteria stringCriteria = new StringCriteria();
        stringCriteria.setEquals(name);
        candidatesExample.setName(stringCriteria);
        PageResult<Candidates> byExample = candidatesService.findByExample(candidatesExample);
//        System.out.println(byExample.getRecordsTotal());
//        System.out.println(byExample.getRecordsFiltered());
//        ListIterator<Candidates> candidatesListIterator = byExample.getData().listIterator();
//        while (candidatesListIterator.hasNext()){
//            Candidates next = candidatesListIterator.next();
//            System.out.println(next);
//        }
//        System.out.println(byExample.getData());
//        Candidates candidates = null;
        return byExample;
    }
    @PostMapping(value = "/saveAll" , headers = "content-type=multipart/form-data")
    @ApiOperation(value = "批量导入简历", notes = "上传excel")
    public PageResult<Candidates> queryByName(@ApiParam(value="excel", required = true) MultipartFile file) {
        System.out.println("request to save Candidates by file");
        log.debug("request to save Candidates by file" );
        //上传过来的文件
        try {
            //获取文件输入流
            InputStream inputStream = file.getInputStream();
            //调用方法进行读取
            EasyExcel.read(inputStream, CandidateData.class, new CandidatesExcelListener(candidatesService)).sheet().doRead();
            return candidatesService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candidatesService.findAll();
    }
    
}
