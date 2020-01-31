package com.bizbox.Service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizbox.apis.JusoApi;
import com.bizbox.dao.PopulationByDAO;
import com.bizbox.dao.SalesDAO;
import com.bizbox.utils.AddressUtil;
import com.bizbox.vo.PopulationByDong;
import com.bizbox.vo.PopulationByLocation;
import com.bizbox.vo.PopulationByTime;
import com.bizbox.vo.SalesInformation;

import lombok.extern.slf4j.Slf4j;

@Service
public class SalesServiceImpl implements SalesService {

	@Autowired
	SalesDAO dao;

	@Override
	public List<SalesInformation> salesInfo(String address) throws Exception {
		return dao.salesInfo(address);
	}

	public List<SalesInformation> salesInfosub(String address) throws Exception {
		List<SalesInformation> list = new LinkedList<SalesInformation>();
		AddressUtil util = new AddressUtil();
		JusoApi juso = new JusoApi();
		String preaddress = util.RemoveNumber(address);

		List<String> adlist = juso.getAddressSetByName(preaddress);
		list = salesInfo(address);
		if (list.isEmpty()) {
			for (int i = 0; i < adlist.size(); i++) {
				list = salesInfo(adlist.get(i));
				if (!list.isEmpty())
					break;
			}
		}
		if (list.isEmpty()) {
			list.add(new SalesInformation("0", "0", "0", address, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
					"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"));
		}
		String standard=list.get(0).getD();
		System.out.println(standard);
		for(int i=list.size()-1; i>=0; i--) {
			if(!list.get(i).getD().equals(standard)) {
				list.remove(i);
			}
		}
		System.out.println(list.size());
//		for (SalesInformation string : list) {
//			System.out.println(string.getF());
//		}
		return list;
	}

	@Override
	public String salesInfosub2(String address) {
		AddressUtil util = new AddressUtil();
		JusoApi juso = new JusoApi();
		String preaddress = util.RemoveNumber(address);
		return null;
	}
}
