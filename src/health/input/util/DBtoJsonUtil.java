/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.util;

import device.input.jsonmodels.JsonDevice;
import health.database.DAO.DatastreamDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.DatastreamUnits;
import health.database.models.DeviceBinding;
import health.database.models.Follower;
import health.database.models.Subject;
import health.input.jsonmodels.JsonDatastream;
import health.input.jsonmodels.JsonDatastreamBlock;
import health.input.jsonmodels.JsonDatastreamUnits;
import device.input.jsonmodels.JsonDeviceBinding;
import health.database.models.UserAvatar;
import health.database.models.Users;
import health.database.models.merge.UserInfo;
import health.input.jsonmodels.JsonFollower;
import health.input.jsonmodels.JsonSubject;
import health.input.jsonmodels.JsonUser;
import health.input.jsonmodels.JsonUserAvatar;
import health.input.jsonmodels.JsonUserInfo;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.DateUtil;

/**
 *
 * @author Leon
 */
public class DBtoJsonUtil {

    public JsonDatastreamBlock convert_a_Datablock(DatastreamBlocks block) {
        JsonDatastreamBlock jblock = new JsonDatastreamBlock();
        jblock.setBlockid(block.getBlockId());
        jblock.setBlockdesc(block.getBlockDesc());
        jblock.setBlockname(block.getDisplayName());
        jblock.setCreated_time(Long.toString(block.getCreated().getTime()));
        jblock.setUpdate_time(Long.toString(block.getUpdated().getTime()));
        return jblock;
    }

    public JsonSubject convert_a_Subject(Subject subject) throws ParseException {
        JsonSubject jsub = new JsonSubject();
        jsub.setId(subject.getId());
        jsub.setTitle(subject.getTitle());
        jsub.setPrivate_set(subject.getPrivateSet());
        jsub.setDescription(subject.getDescription());
        jsub.setLoginid(subject.getLoginID());
        jsub.setParent_subject(subject.getParentSub());
        jsub.setTags(subject.getTags());
        jsub.setStatus(subject.getStatus());
        jsub.setCreated_time(DateUtil.toMillisecFormatString(subject.getCreatedTime()));
        jsub.setUpdated_time(DateUtil.toMillisecFormatString(subject.getCreatedTime()));
        jsub.setIcon(subject.getIcon());
        return jsub;
    }

    public JsonUserInfo convert_a_userinfo(UserInfo userinfo,Map<String,String> followerMap,Map<String,String> followingMap) {
        JsonUserInfo juserinfo = new JsonUserInfo();
        if(followerMap!=null&&followingMap!=null)
        {
        	juserinfo.setIs_follower("null");
        	juserinfo.setIs_following("null");
        	if(followerMap.containsKey((String)userinfo.getUser().getLoginID()))
        	{
        		juserinfo.setIs_follower("true");
        	}
        	else{
        		juserinfo.setIs_follower("false");
        	}
        	if(followingMap.containsKey((String)userinfo.getUser().getLoginID()))
        	{
        		juserinfo.setIs_following("true");
        	}else{
        		juserinfo.setIs_following("false");
        	}
        }
        juserinfo.setUser(convert_a_user(userinfo.getUser()));
        if (userinfo.getAvatar() != null) {
            juserinfo.setAvatar(convert_a_userAvatar(userinfo.getAvatar()));
        }
        return juserinfo;
    }
    public JsonUserInfo convert_a_userinfo(UserInfo userinfo) {
        JsonUserInfo juserinfo = new JsonUserInfo();
        
        juserinfo.setUser(convert_a_user(userinfo.getUser()));
        if (userinfo.getAvatar() != null) {
            juserinfo.setAvatar(convert_a_userAvatar(userinfo.getAvatar()));
        }
        return juserinfo;
    }
    public JsonUser convert_a_user(Users user) {
        JsonUser juser = new JsonUser();
        juser.setLoginid(user.getLoginID());
        juser.setScreenname(user.getScreenname());
        juser.setCreated_time(user.getCreatedTime());
        return juser;
    }

    public JsonUserAvatar convert_a_userAvatar(UserAvatar avatar) {
        JsonUserAvatar javar = new JsonUserAvatar();
        javar.setLoginid(avatar.getLoginID());
        javar.setUrl(avatar.getUrl());
        javar.setHash(avatar.getHash());
        return javar;
    }

    public JsonFollower convert_a_Follower(Follower follower) throws ParseException {
        JsonFollower jfollwer = new JsonFollower();
        jfollwer.setLoginid(follower.getLoginID());
        jfollwer.setFollower_id(follower.getFollowerID());
        jfollwer.setNote(follower.getNote());
        jfollwer.setStatus(follower.getStatus());
        return jfollwer;
    }

    public JsonSubject convertSubjectContainDatastreams(Subject subject) throws ParseException {
        JsonSubject jsub = new JsonSubject();
        jsub.setId(subject.getId());
        DatastreamDAO dstreamDao = new DatastreamDAO();
        List<Datastream> dstreamList = dstreamDao.getDatastreamList(subject.getId(), true, false);
        List<JsonDatastream> jdstreamList = new ArrayList<JsonDatastream>();
        for (int i = 0; i < dstreamList.size(); i++) {
            JsonDatastream jsonDs = convertDatastream(dstreamList.get(i), null);
            jdstreamList.add(jsonDs);
        }
        jsub.setDatastreams(jdstreamList);
        jsub.setTitle(subject.getTitle());
        jsub.setPrivate_set(subject.getPrivateSet());
        return jsub;
    }

    public JsonDatastream convertDatastream(Datastream datastream, Map<String, String> onlyListedUnitIDs) throws ParseException {
        JsonDatastream jd = new JsonDatastream();
        List<DatastreamUnits> unitsList = datastream.getDatastreamUnitsList();
        List<JsonDatastreamUnits> jdatastreamUnitList = new ArrayList<JsonDatastreamUnits>();
        jd.setDatastream_id(datastream.getStreamId());
        jd.setOwner(datastream.getOwner());
        jd.setNote(datastream.getNote());
        jd.setCreated_time(DateUtil.toMillisecFormatString(datastream.getCreatedTime()));
        jd.setSubject_id(datastream.getSubId());
        jd.setTitle(datastream.getTitle());
        System.out.print("title:" + jd.getTitle() + "," + datastream.getTitle());
        jd.setDesc(datastream.getDescription());
        jd.setIcon(datastream.getIcon());
        int totalblocks = 0;
        if (datastream.getDatastreamBlocksList() != null) {
            totalblocks = datastream.getDatastreamBlocksList().size();
        }
        jd.setTotal_blocks(totalblocks);
        int total_units = 0;
        if (unitsList != null) {
            total_units = unitsList.size();
        }
        jd.setTotal_units(total_units);
        if (datastream.getUpdated() != null) {
            jd.setUpdated_time(DateUtil.toMillisecFormatString(datastream.getUpdated()));
        }
        for (int i = 0; i < unitsList.size(); i++) {
            JsonDatastreamUnits unit = new JsonDatastreamUnits();
            unit.setUnit_id(unitsList.get(i).getUnitID());
            unit.setMax_value(unitsList.get(i).getMaxValue());
            unit.setMin_value(unitsList.get(i).getMinValue());
            unit.setUnit_label(unitsList.get(i).getUnitLabel());
            unit.setUnit_symbol(unitsList.get(i).getUnitSymbol());
            unit.setUnit_type(unitsList.get(i).getUnitType());
            unit.setValue_type(unitsList.get(i).getValueType());
            if (onlyListedUnitIDs != null && onlyListedUnitIDs.size() > 0) {
                if (onlyListedUnitIDs.get(unitsList.get(i).getUnitID()) == null) {
                } else {
                    jdatastreamUnitList.add(unit);
                }
            } else {
                jdatastreamUnitList.add(unit);
            }
        }
        jd.setUnits_list(jdatastreamUnitList);
        return jd;
    }

    public JsonDevice convertDatastreamToJsonDevice(Datastream datastream, Map<String, String> onlyListedUnitIDs) throws ParseException {
        JsonDevice jd = new JsonDevice();
        List<DatastreamUnits> unitsList = datastream.getDatastreamUnitsList();
        List<JsonDatastreamUnits> jdatastreamUnitList = new ArrayList<JsonDatastreamUnits>();
        jd.setDevice_id(datastream.getStreamId());
        jd.setOwner(datastream.getOwner());
        jd.setNote(datastream.getNote());
        jd.setCreated_time(DateUtil.toMillisecFormatString(datastream.getCreatedTime()));
        jd.setDevice_name(datastream.getTitle());
//        System.out.print("title:" + jd.getTitle() + "," + datastream.getTitle());
        jd.setDesc(datastream.getDescription());
        jd.setIcon(datastream.getIcon());
        if (datastream.getUpdated() != null) {
            jd.setUpdated_time(DateUtil.toMillisecFormatString(datastream.getUpdated()));
        }
        for (int i = 0; i < unitsList.size(); i++) {
            JsonDatastreamUnits unit = new JsonDatastreamUnits();
            unit.setUnit_id(unitsList.get(i).getUnitID());
            unit.setMax_value(unitsList.get(i).getMaxValue());
            unit.setMin_value(unitsList.get(i).getMinValue());
            unit.setUnit_label(unitsList.get(i).getUnitLabel());
            unit.setUnit_symbol(unitsList.get(i).getUnitSymbol());
            unit.setUnit_type(unitsList.get(i).getUnitType());
            unit.setValue_type(unitsList.get(i).getValueType());
            if (onlyListedUnitIDs != null && onlyListedUnitIDs.size() > 0) {
                if (onlyListedUnitIDs.get(unitsList.get(i).getUnitID()) == null) {
                } else {
                    jdatastreamUnitList.add(unit);
                }
            } else {
                jdatastreamUnitList.add(unit);
            }
        }
        jd.setUnits_list(jdatastreamUnitList);
        return jd;
    }

    public HashMap<String, String> ToDatastreamUnitsMap(Datastream datastream) {
        List<DatastreamUnits> dsUnitList = datastream.getDatastreamUnitsList();
        HashMap<String, String> mapUnits = new HashMap<String, String>();
        for (DatastreamUnits unit : dsUnitList) {
            mapUnits.put(unit.getUnitID(), unit.getUnitID());
        }
        return mapUnits;
    }

    public JsonDeviceBinding convertDeviceSerial(DeviceBinding device) throws ParseException {
        JsonDeviceBinding jDevice = new JsonDeviceBinding();
        jDevice.setSerial_id(device.getSerialID());
        jDevice.setActive_by(device.getActiveBy());
        jDevice.setTemplate_id(device.getTemplateid());
        jDevice.setDevice_model(device.getDeviceModel());
        jDevice.setDevice_type(device.getDeviceType());
        jDevice.setDevice_vendor(device.getDeviceVendor());
        if (device.getActiveTime() != null) {
            jDevice.setActive_time(Long.toString(device.getActiveTime().getTime()));
        }
        if (device.getCreatedTime() != null) {
            jDevice.setCreated_time(Long.toString(device.getCreatedTime().getTime()));
        }
        return jDevice;
    }
}
