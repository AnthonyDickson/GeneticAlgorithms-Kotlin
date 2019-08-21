import React, {Component} from "react";
import PropTypes from "prop-types";

import {List, Skeleton, Tooltip} from "antd";
import {Link} from "react-router-dom";
import TimeAgo from "timeago-react";

class RunsList extends Component {
    static propTypes = {
        loading: PropTypes.bool.isRequired,
        runs: PropTypes.arrayOf(PropTypes.object).isRequired,
    };

    render() {
        const {runs, loading} = this.props;

        return loading ?
            <List bordered>
                <List.Item>
                    <Skeleton active/>
                </List.Item>
            </List>
            :
            <List
                bordered
                dataSource={runs}
                renderItem={item => (
                    <List.Item>
                        <Link to={`/runs/${item.id}`}>Run #{item.id}</Link>
                        {' - '}
                        <Tooltip title={new Date(item.create_time).toLocaleString()}>
                            <TimeAgo datetime={item.create_time}/>
                        </Tooltip>
                    </List.Item>
                )}
            />
    }
}

export default RunsList;