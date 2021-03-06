/**
 * 
 */
export class NameIdentifier{
	
	constructor(json){
		if(json == undefined){
			return;
		}
		this.nameIdentifier = json.$t;
		this.nameIdentifierScheme = json.nameIdentifierScheme;
		this.schemeURI = json.schemeURI;
	}
	
	getNameIdentifier(){
		return this.nameIdentifier;
	}
	getNameIdentifierScheme(){
		return this.nameIdentifierScheme;
	}
	getSchemeURI(){
		return this.schemeURI;
	}
	
	setNameIdentifier(name){
		this.nameIdentifier = name;
	}
	setNameIdentifierScheme(scheme){
		this.nameIdentifierScheme = scheme;
	}
	setSchemeURI(uri){
		this.schemeURI = uri;
	}
}