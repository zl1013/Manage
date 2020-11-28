package com.ctsi.zz.service.impl;


import org.springframework.stereotype.Service;

import java.lang.String;

import com.ctsi.zz.domain.Candidates;
import com.ctsi.zz.domain.CandidatesExample;
import com.ctsi.zz.service.CandidatesService;
import com.ctsi.zz.repository.CandidatesRepository;

import com.ctsi.ssdc.service.impl.StrengthenBaseServiceImpl;

/**
 * Service Implementation for managing Candidates.
 *
 * @author ctsi-biyi-generator
 *
 */
@Service
public class CandidatesServiceImpl 
	extends StrengthenBaseServiceImpl<CandidatesRepository, Candidates, String, CandidatesExample> 
	implements CandidatesService {


}
