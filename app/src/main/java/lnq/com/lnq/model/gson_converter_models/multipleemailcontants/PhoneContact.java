package lnq.com.lnq.model.gson_converter_models.multipleemailcontants;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PhoneContact {

    ContentResolver cr;
    List<Contact> contactList;
    Context context;

    public PhoneContact(Context context) {
        this.context = context;
        if (context != null) {
            cr = context.getContentResolver();
            contactList = new ArrayList<Contact>();
            readContacts();
        }
    }

    public void readContacts() {

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Contact contact = new Contact();

                    // Get contact id (id)
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    contact.setId(id);

                    // Get contact name (displayName)
                    String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (displayName.equalsIgnoreCase("Waqas Arshad @ Lead Concept")) {
                        Log.d("PhoneNumber", displayName);
                    }
                    contact.setDisplayName(displayName);

                    // Get BirthDay (dateOfBirth)
//                    Uri URI_DOB = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_DOB = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND "
//                            + ContactsContract.Data.MIMETYPE
//                            + " = ? AND "
//                            + ContactsContract.CommonDataKinds.Event.TYPE
//                            + "="
//                            + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
//                    String[] SELECTION_ARRAY_DOB = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
//
//                    Cursor currDOB = cr.query(URI_DOB, null, SELECTION_DOB, SELECTION_ARRAY_DOB, null);
//                    int indexDob = currDOB.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
//                    if (currDOB.moveToNext()) {
//                        String dobStr = currDOB.getString(indexDob);
//                        contact.setDateOfBirth(dobStr);
//                    }
//                    currDOB.close();


                    // Get Anniversary (dateOfAnniversary)
//                    Uri URI_DOA = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_DOA = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND "
//                            + ContactsContract.Data.MIMETYPE
//                            + " = ? AND "
//                            + ContactsContract.CommonDataKinds.Event.TYPE
//                            + "="
//                            + ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY;
//                    String[] SELECTION_ARRAY_DOA = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
//
//                    Cursor currDOA = cr.query(URI_DOA, null, SELECTION_DOA, SELECTION_ARRAY_DOA, null);
//                    int indexDoa = currDOA.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
//                    if (currDOA.moveToNext()) {
//                        String doaStr = currDOA.getString(indexDoa);
//                        contact.setDateOfAnniversary(doaStr);
//                    }
//                    currDOA.close();


                    // Get Nick Nmae(nickName)
//                    Uri URI_NICK_NAME = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_NICK_NAME = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND "
//                            + ContactsContract.Data.MIMETYPE
//                            + " = ?";
//                    String[] SELECTION_ARRAY_NICK_NAME = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE};
//
//                    Cursor currNickName = cr.query(URI_NICK_NAME, null,
//                            SELECTION_NICK_NAME, SELECTION_ARRAY_NICK_NAME,
//                            null);
//
//                    int indexNickName = currNickName.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME);
//                    if (currNickName.moveToNext()) {
//                        String nickNameStr = currNickName
//                                .getString(indexNickName);
//                        contact.setNickName(nickNameStr);
//                    }
//                    currNickName.close();


                    // GetNote(note)
//                    Uri URI_NOTE = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_NOTE = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND " + ContactsContract.Data.MIMETYPE
//                            + " = ?";
//                    String[] SELECTION_ARRAY_NOTE = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//
//                    Cursor currNote = cr.query(URI_NOTE, null, SELECTION_NOTE, SELECTION_ARRAY_NOTE, null);
//                    int indexNote = currNote.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE);
//                    if (currNote.moveToNext()) {
//                        String noteStr = currNote.getString(indexNote);
//                        contact.setNote(noteStr);
//                    }
//                    currNote.close();


                    // Get User Image (image)
//                    Uri URI_PHOTO = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_PHOTO = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND " + ContactsContract.Data.MIMETYPE
//                            + " = ?";
//                    String[] SELECTION_ARRAY_PHOTO = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};
//
//                    Cursor currPhoto = cr.query(URI_PHOTO, null, SELECTION_PHOTO, SELECTION_ARRAY_PHOTO, null);
//                    int indexPhoto = currPhoto.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);
//
//                    while (currPhoto.moveToNext()) {
//
//                        byte[] photoByte = currPhoto.getBlob(indexPhoto);
//
//                        if (photoByte != null) {
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
//
//                            // Getting Caching directory
//                            File cacheDirectory = context.getCacheDir();
//
//                            // Temporary file to store the contact image
//                            // File tmpFile = new File(cacheDirectory.getPath()
//                            // + "/image_"+id+".png");
//                            File tmpFile = new File(cacheDirectory.getPath() + "/image_.png");
//
//                            // The FileOutputStream to the temporary file
//                            try {
//                                FileOutputStream fOutStream = new FileOutputStream(tmpFile);
//
//                                // Writing the bitmap to the temporary file as png file
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);
//
//                                // Flush the FileOutputStream
//                                fOutStream.flush();
//
//                                // Close the FileOutputStream
//                                fOutStream.close();
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            // String photoPath = tmpFile.getPath();
//                            contact.setImage(bitmap);
//                        }
//                    }
//                    currPhoto.close();


                    // Get Email and Type.... (<HashMap<Integer, String> emails)
                    Uri URI_EMAIL = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                    String SELECTION_EMAIL = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
                    String[] SELECTION_ARRAY_EMAIL = new String[]{id};

                    Cursor emailCur = cr.query(URI_EMAIL, null, SELECTION_EMAIL, SELECTION_ARRAY_EMAIL, null);
                    int indexEmail = emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                    int indexEmailType = emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);

                    if (emailCur.getCount() > 0) {

                        HashMap<Integer, String> emailMap = new HashMap<Integer, String>();

                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses,
                            // if the email addresses were stored in an array
                            String emailStr = emailCur.getString(indexEmail);
                            String emailTypeStr = emailCur.getString(indexEmailType);
                            emailMap.put(Integer.parseInt(emailTypeStr != null ? emailTypeStr : String.valueOf(Contact.Email_TYPE.HOME)), emailStr);
                        }
                        contact.setEmails(emailMap);
                    }
                    emailCur.close();

                    // Get Phone Number....(HashMap<Integer, String>phones)
                    Uri URI_PHONE = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String SELECTION_PHONE = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                    String[] SELECTION_ARRAY_PHONE = new String[]{id};

                    Cursor currPhone = cr.query(URI_PHONE, null, SELECTION_PHONE, SELECTION_ARRAY_PHONE, null);
                    int indexPhoneNo = currPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexPhoneType = currPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);

                    if (currPhone.getCount() > 0) {
                        HashMap<Integer, String> phoneMap = new HashMap<Integer, String>();
                        int count = 1;
                        phoneMap.put(count, "");
                        while (currPhone.moveToNext()) {
                            String phoneNoStr = currPhone.getString(indexPhoneNo);
                            String phoneTypeStr = currPhone.getString(indexPhoneType);
                            for (Map.Entry<Integer, String> entry : phoneMap.entrySet()) {
                                String value = entry.getValue();
                                if (!value.equals(phoneNoStr)) {
                                    phoneMap.put(count, phoneNoStr.replaceAll(" ", ""));
                                    count++;
                                    break;
                                }
                            }
                        }
                        contact.setPhones(phoneMap);
                    }
                    currPhone.close();

                    // Get Postal Address....(HashMap<Integer, Address> addresses)
//                    Uri URI_ADDRESS = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_ADDRESS = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND " + ContactsContract.Data.MIMETYPE
//                            + " = ?";
//                    String[] SELECTION_ARRAY_ADDRESS = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
//
//                    Cursor currAddr = cr.query(URI_ADDRESS, null, SELECTION_ADDRESS, SELECTION_ARRAY_ADDRESS, null);
//                    int indexAddType = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
//                    int indexStreet = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET);
//                    int indexPOBox = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
//                    int indexNeighbor = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD);
//                    int indexCity = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY);
//                    int indexRegion = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION);
//                    int indexPostCode = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
//                    int indexCountry = currAddr
//                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
//
//                    if (currAddr.getCount() > 0) {
//                        HashMap<Integer, Contact.Address> addressMap = new HashMap<Integer, Contact.Address>();
//                        while (currAddr.moveToNext()) {
//
//                            Contact.Address address = new Contact.Address();
//
//                            String typeStr = currAddr.getString(indexAddType);
//
//                            address.setStreet(currAddr.getString(indexStreet));
//                            address.setNeighborhood(currAddr.getString(indexNeighbor));
//                            address.setPostalCode(currAddr.getString(indexPostCode));
//                            address.setPostBox(currAddr.getString(indexPOBox));
//                            address.setCity(currAddr.getString(indexCity));
//                            address.setState(currAddr.getString(indexRegion));
//                            address.setCountry(currAddr.getString(indexCountry));
//
//                            addressMap.put(Integer.parseInt(typeStr), address);
//                        }
//                        contact.setAddresses(addressMap);
//                    }
//                    currAddr.close();


                    // Get Organization (HashMap<Integer, Organization> organizations)
//                    Uri URI_ORGNIZATION = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_ORGNIZATION = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND "
//                            + ContactsContract.Data.MIMETYPE
//                            + " = ?";
//                    String[] SELECTION_ARRAY_ORGNIZATION = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
//
//                    Cursor currOrg = cr.query(URI_ORGNIZATION, null,
//                            SELECTION_ORGNIZATION, SELECTION_ARRAY_ORGNIZATION,
//                            null);
//                    int indexOrgType = currOrg
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE);
//                    int indexOrgName = currOrg
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA);
//                    int indexOrgTitle = currOrg
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE);
//
//                    if (currOrg.getCount() > 0) {
//                        HashMap<Integer, Contact.Organization> orgMap = new HashMap<Integer, Contact.Organization>();
//                        while (currOrg.moveToNext()) {
//
//                            Contact.Organization organization = new Contact.Organization();
//
//                            String orgTypeStr = currOrg.getString(indexOrgType);
//
//                            organization.setCompany(currOrg.getString(indexOrgName));
//                            organization.setJobTitle(currOrg.getString(indexOrgTitle));
//
//                            orgMap.put(Integer.parseInt(orgTypeStr), organization);
//                        }
//                        contact.setOrganizations(orgMap);
//                    }
//                    currOrg.close();


                    // Get Instant Messenger..... (HashMap<Integer, String> im)
//                    Uri URI_IM = ContactsContract.Data.CONTENT_URI;
//                    String SELECTION_IM = ContactsContract.Data.CONTACT_ID
//                            + " = ? AND " + ContactsContract.Data.MIMETYPE
//                            + " = ?";
//                    String[] SELECTION_ARRAY_IM = new String[]{
//                            id,
//                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
//
//                    Cursor currIM = cr.query(URI_IM, null, SELECTION_IM, SELECTION_ARRAY_IM, null);
//                    int indexName = currIM
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA);
//                    int indexType = currIM
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL);
//
//                    if (currIM.getCount() > 0) {
//                        HashMap<Integer, String> imMap = new HashMap<Integer, String>();
//                        while (currIM.moveToNext()) {
//                            String imNameStr = currIM.getString(indexName);
//                            String imTypeStr = currIM.getString(indexType);
//
//                            imMap.put(Integer.parseInt(imTypeStr), imNameStr);
//                        }
//                        contact.setIm(imMap);
//                    }
//                    currIM.close();
                    /*****************************************/
                    int foundCounter = 1;
                    outer:
                    for (int i = 0; i < contactList.size(); i++) {
                        Contact con = contactList.get(i);
                        if (con.getPhones() != null) {
                            for (Map.Entry<Integer, String> entryNumber : con.getPhones().entrySet()) {
                                String number = entryNumber.getValue();
                                if (contact.getPhones() != null) {
                                    for (Map.Entry<Integer, String> numbers : contact.getPhones().entrySet()) {
                                        String matchedNumber = numbers.getValue();
                                        if (number.equalsIgnoreCase(matchedNumber)) {
                                            foundCounter = 0;
                                            break outer;
                                        } else {
                                            foundCounter = 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (foundCounter == 1)
                        contactList.add(contact);
                }
            }
        }
        cur.close();
    }

    public List<Contact> getAllContacts() {
        return contactList;
    }

}