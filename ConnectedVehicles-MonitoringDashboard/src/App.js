import React, { Component } from 'react'
import axios from 'axios'
import ReactTable from "react-table";
import 'react-table/react-table.css'
import {matchSorter} from 'match-sorter'
import { Helmet } from 'react-helmet'

const TITLE = 'Connected Vehicles Monitoring Application'

export class TitleComponent extends React.PureComponent {
  render () {
    return (
      <>
        <Helmet>
          <title>{ TITLE }</title>
        </Helmet>        
      </>
    )
  }
}



export default class App extends Component {
	 intervalID;

  constructor(props){
    super(props)
    this.state = {
      vehicles: [],
	  customers: [],
      loading:true
    }
  }
  
  async getData()
  {
	this.getVehiclesData()
	this.intervalID = setTimeout(this.getData.bind(this), 5000);
  }
  
  async getVehiclesData(){
    const res = await axios.get('/connected_vehicles/vehicle')
    console.log(res.data)
    this.setState({loading:false, vehicles: res.data})
  }
  
  componentDidMount(){
	document.title = 'Connected Vehicles - Monitoing Application';
	this.getData()
  }
  
  
  componentWillUnmount() {
        /*
          stop getData() from continuing to run even
          after unmounting this component
        */
        clearInterval(this.intervalID);
      }
  render() {
    const columns = [{  
      Header: 'Vehicle ID/VIN',  
      accessor: 'vehicleId',
     }
     ,{  
      Header: 'Registration No',  
      accessor: 'regNo' ,
      }
      
     ,{  
     Header: 'Customer ID',  
     accessor: 'customerId' ,
     }
     ,{  
     Header: 'Ping Date/Time',  
     accessor: 'pingDtm',
     },
	 {  
     Header: 'Connection Status',  
     accessor: 'connectionStatus',
     }
  ]
    return (
	
	<div className="App">
	<h1 >Connected Vehicles Monitoring Application</h1>
    	  <ReactTable 
		  filterable
          defaultFilterMethod={(filter, row) =>
            String(row[filter.id]) === filter.value}
      data={this.state.vehicles}  
      columns={[
            {
              
              columns: [
                {
                  Header: "Vehicle ID",
                   id: "vehicleId",
                  accessor: d => d.vehicleId,
                  filterMethod: (filter, row) =>
                    row[filter.id].startsWith(filter.value) &&
                    row[filter.id].endsWith(filter.value)
                },
                {
                  Header: "Registration No",
                  id: "regNo",
                  accessor: d => d.regNo,
                  filterMethod: (filter, rows) =>
                    matchSorter(rows, filter.value, { keys: ["regNo"] }),
                  filterAll: true
                },
                {
                  Header: "Customer ID",
                  id: "customerId",
                  accessor: d => d.customerId,
                  filterMethod: (filter, rows) =>
                    matchSorter(rows, filter.value, { keys: ["customerId"] }),
                  filterAll: true
                },
                {
                  Header: "Ping Date/Time",
                  id: "pingDtm",
                  accessor: d => d.pingDtm,
                  filterMethod: (filter, rows) =>
                    matchSorter(rows, filter.value, { keys: ["pingDtm"] }),
                  filterAll: true
                },
                {
                  Header: "Connection Status",
                  id: "connectionStatus",
                  accessor: d => d.connectionStatus,
				   Cell: ({ value }) => (value === "CONNECTED" ? "CONNECTED" : "NOT CONNECTED"),
                  filterMethod: (filter, row) => {
                    if (filter.value === "all") {
                      return true;
                    }
                    if (filter.value === "CONNECTED") {
                      return row[filter.id] === "CONNECTED";
                    }
                    return row[filter.id] === "NOT CONNECTED";
                  
				  }
				  ,
                  Filter: ({ filter, onChange }) =>
                    <select
                      onChange={event => onChange(event.target.value)}
                      style={{ width: "100%" }}
                      value={filter ? filter.value : "all"}
                    >
                      <option value="all">Show All</option>
                      <option value="CONNECTED">CONNECTED</option>
                      <option value="NOT CONNECTED">NOT CONNECTED</option>
                    </select>
                }
								  
                
              ]
            }
          ]}
		  className="-striped -highlight"
		  defaultPageSize={10}
 
   />
      </div>
	
	
      
    )
  }
}

