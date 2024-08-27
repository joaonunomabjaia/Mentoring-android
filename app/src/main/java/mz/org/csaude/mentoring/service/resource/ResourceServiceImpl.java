package mz.org.csaude.mentoring.service.resource;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.resource.ResourceDAO;
import mz.org.csaude.mentoring.model.resourceea.Node;
import mz.org.csaude.mentoring.model.resourceea.Resource;

public class ResourceServiceImpl extends BaseServiceImpl<Resource> implements ResourceService {

    private ResourceDAO resourceDAO;
    public ResourceServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.resourceDAO = getDataBaseHelper().getResourceDAO();
    }

    @Override
    public void savedOrUpdateResource(Resource resource) throws SQLException {
          Resource r = this.resourceDAO.getByUuid(resource.getUuid());
          if(r!=null) {
              resource.setId(r.getId());
          }
          this.resourceDAO.createOrUpdate(resource);
    }

    @Override
    public void savedOrUpdateResources(List<Resource> resources) throws SQLException {
          for (Resource resource :resources){
              this.savedOrUpdateResource(resource);
          }
    }

    @Override
    public Resource save(Resource record) throws SQLException {

        this.resourceDAO.insert(record);
        return record;
    }

    @Override
    public Resource update(Resource record) throws SQLException {

        this.resourceDAO.update(record);
        return record;
    }

    @Override
    public int delete(Resource record) throws SQLException {
        return this.resourceDAO.delete(record);
    }

    @Override
    public List<Resource> getAll() throws SQLException {
        return this.resourceDAO.queryForAll();
    }
    @Override
    public Resource getById(int id) throws SQLException {
        return this.resourceDAO.queryForId(id);
    }


    public List<JSONObject> getChildrenWithNameAndDescription(JSONArray jsonArray, String parentLabel, String grandparentLabel, String greatGrandparentLabel) throws JSONException {
        List<JSONObject> result = new ArrayList<>();
        traverseJSON(jsonArray, result, parentLabel, grandparentLabel, greatGrandparentLabel);
        return result;
    }

    private static void traverseJSON(JSONArray jsonArray, List<JSONObject> result, String parentLabel, String grandparentLabel, String greatGrandparentLabel) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String currentLabel = jsonObject.optString("label");
            if (jsonObject.has("children")) {
                traverseJSON(jsonObject.getJSONArray("children"), result, currentLabel, parentLabel, grandparentLabel);
            } else if (jsonObject.has("name") && jsonObject.has("description")) {
                jsonObject.put("parentLabel", parentLabel);
                jsonObject.put("grandparentLabel", grandparentLabel);
                jsonObject.put("greatGrandparentLabel", greatGrandparentLabel);
                result.add(jsonObject);
            }
        }
    }

    public List<Node> convertToNodeList(List<JSONObject> jsonObjects) throws JSONException {
        List<Node> nodeList = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            Node node = new Node();
            node.setName(jsonObject.getString("name"));
            node.setDescription(jsonObject.getString("description"));
            node.setLabel(jsonObject.optString("label"));
            node.setClickable(jsonObject.optInt("clickable"));
            node.setIcon(jsonObject.optString("icon"));
            node.setProgram(jsonObject.optString("greatGrandparentLabel"));
            node.setCategory(jsonObject.optString("grandparentLabel"));
            node.setSubCategory(jsonObject.optString("parentLabel"));
            node.setResource(jsonObject.optString("resource"));
            node.setType(jsonObject.optString("type"));
            nodeList.add(node);
        }
        return nodeList;
    }


}
