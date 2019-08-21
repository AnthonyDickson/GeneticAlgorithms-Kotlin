import './App.css';

import {Button, Layout, Modal, Result, Skeleton, Typography} from 'antd';

import React, {Component} from 'react';
import {Link, Redirect, Route, Switch} from 'react-router-dom';

import RunsList from "./components/RunsList";
import Breadcrumbs from "./components/breadcrumbs";

class App extends Component {
    state = {
        loading: true,
        runs: []
    };

    componentDidMount() {
        this.fetchRuns()
    }

    fetchRuns = () => {
        this.setState({loading: true});
        const url = '/api/runs';

        fetch(url)
            .then(res => {
                if (res.status !== 200) throw Error(`${res.status}: ${res.statusText}`);

                return res.json();
            })
            .then(res => {
                this.setState({runs: res.runs});
            })
            .catch(err => {
                console.log(err);
                Modal.error({
                    title: 'Error: Could not load data!',
                    content: `Request to '${url}' failed. Reason: '${err.message}'.`,
                });
            }).finally(() => {
            this.setState({loading: false})
        });
    };

    render() {
        return (
            <Layout>
                <Layout.Header style={{background: '#fff', padding: '24px 16px', minHeight: 120}}>
                    <Typography>
                        <Typography.Title>Genetic Algorithms</Typography.Title>
                        <Breadcrumbs/>
                    </Typography>
                </Layout.Header>
                <Layout.Content style={{margin: '24px 16px 0'}}>
                    <div style={{padding: 24, background: '#fff', minHeight: 360}}>
                        <Switch>
                            <Route exact path="/" render={() => <Redirect to={"/runs"}/>}/>
                            <Route path="/runs/:id" component={this.Run}/>
                            <Route path="/runs" render={() => <RunsList runs={this.state.runs}
                                                                        loading={this.state.loading}/>
                            }
                            />
                            <Route component={this.NotFound}/>
                        </Switch>
                    </div>
                </Layout.Content>
                <Layout.Footer style={{textAlign: 'center'}}>Ant Design ©2018 Created by Ant UED</Layout.Footer>
            </Layout>
        );
    }

    Run = ({match}) => {
        if (this.state.loading) {
            return <Skeleton active/>
        }

        const runId = parseInt(match.params.id);
        const runIdExists = this.state.runs.findIndex(element => element.id === runId) !== -1;

        // TODO: Display run data and stats
        return (
            runIdExists ?
                <div>
                    <h2>{runId}</h2>
                    <p>Placeholder text.</p>
                </div>
                :
                this.NotFound()
        );
    };

    NotFound = () => {
        return <Result
            status="404"
            title="404"
            subTitle="Sorry, the page you visited does not exist."
            extra={<Button type="primary"><Link to="/">Back Home</Link></Button>}
        />
    };
}

export default App;